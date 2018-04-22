package com.rozproszone.zad1.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTcp {
    private final long id;
    private ServerTcp server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientTcp(long id, Socket clientSocket, ServerTcp server) throws IOException {
        this.id = id;
        this.socket = clientSocket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public long getId() {
        return id;
    }

    public ServerTcp getServer() {
        return server;
    }

    public Socket getSocket() {
        return socket;
    }

    public Message receiveMessage() throws IOException {
        String content = reader.readLine();
        if(content == null) {
            throw new IOException();
        }

        System.out.println("TCP receive: " + content);
        return new Message(id, content);
    }

    public void sendMessage(Message message) {
        System.out.println("TCP send message: " + message);
        writer.println(message.toString());
    }

    public void sendText(String text) {
        System.out.println("TCP send text: " + text);
        writer.println(text);
    }
}
