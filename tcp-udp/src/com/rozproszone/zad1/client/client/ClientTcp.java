package com.rozproszone.zad1.client.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTcp implements Client {
    private final long id;
    private final String hostName;
    private final int serverPort;
    private final Socket serverSocket;
    private PrintWriter writer;
    private BufferedReader bufferedReader;

    public ClientTcp(String hostName, int serverPort) throws IOException {
        this.hostName = hostName;
        this.serverPort = serverPort;
        serverSocket = new Socket(hostName, serverPort);
        writer = new PrintWriter(serverSocket.getOutputStream(), true);
        bufferedReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

        id = Long.parseLong(receiveMessage());
    }

    @Override
    public String receiveMessage() throws IOException {
        String line = bufferedReader.readLine();
        if(line == null)
            throw new IOException();
        return line;
    }

    @Override
    public void sendMessage(String message) {
        writer.println(message);
    }

    @Override
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            //
        }
    }

    @Override
    public long getId() {
        return id;
    }
}
