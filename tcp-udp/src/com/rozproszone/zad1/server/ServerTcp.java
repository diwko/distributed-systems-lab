package com.rozproszone.zad1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ServerTcp {
    private final int portNumber;
    private final long serverId = 0;
    private long lastClientId = 1;
    private Map<Long, ClientTcp> clientsTcp = new HashMap<>();
    private BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private ServerSocket serverSocket = null;

    private ExecutorService executorService;

    public ServerTcp(int portNumber, ExecutorService executorService) {
        this.portNumber = portNumber;
        this.executorService = executorService;
    }

    public void start() throws IOException {
        openServerSocket();

        executorService.execute(acceptClientsTask());

        executorService.execute(broadcastMessagesTask());
    }

    public void close() {
        clientsTcp.forEach((k, v) -> disconnectClient(v));

        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openServerSocket() throws IOException {
        serverSocket = new ServerSocket(portNumber);
    }

    private Runnable acceptClientsTask() {
        return () -> {
            while (true) {
                try {
                    acceptClientTcp();
                } catch (IOException e) {
                    System.out.println("ClientTcp accept error!" + e.getMessage());
                }
            }
        };
    }

    private void acceptClientTcp() throws IOException {
        Socket clientSocket = serverSocket.accept();

        ClientTcp client = new ClientTcp(lastClientId++, clientSocket, this);
        clientsTcp.put(client.getId(), client);

        executorService.execute(receiveMessageTask(client));

        sendWelcomeMessage(client);
    }

    private Runnable receiveMessageTask(ClientTcp client) {
        return () -> {
            while (true) {
                try {
                    Message message = client.receiveMessage();
                    messages.add(message);
                } catch (IOException e) {
                    disconnectClient(client.getId());
                    break;
                }
            }
        };
    }

    private void disconnectClient(long clientId) {
        ClientTcp client = clientsTcp.remove(clientId);
        disconnectClient(client);
    }

    private void disconnectClient(ClientTcp client) {
        clientsTcp.remove(client.getId());
        try {
            client.getSocket().close();
        } catch (IOException e) {
            //
        }
        System.out.println("TCP client disconnected, id: " + client.getId());
    }

    private void sendWelcomeMessage(ClientTcp client) {
        client.sendText(String.valueOf(client.getId()));
        client.sendMessage(new Message(serverId, "Connected to server, your id: " + client.getId()));
    }

    private Runnable broadcastMessagesTask() {
        return () -> {
            while (true) {
                try {
                    Message msg = messages.take();
                    broadcastMessage(msg);
                } catch (InterruptedException e) {
                    System.out.println("Cannot receive new Message. " + e.getMessage());
                }
            }
        };
    }

    private void broadcastMessage(Message message) {
        for(ClientTcp client : clientsTcp.values()) {
            if (client.getId() == message.getSenderId()) {
                continue;
            }
            client.sendMessage(message);
        }
    }
}
