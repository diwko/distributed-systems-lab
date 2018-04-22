package com.rozproszone.zad1.client.client;

import java.io.IOException;
import java.net.*;

public class ClientUdp implements Client {
    private final String hostName;
    private final int portNumber;
    private final InetAddress hostAddress;
    private final DatagramSocket serverSocket;
    private final int bufferSize = 1024;
    private final long id;

    public ClientUdp(String hostName, int portNumber, long clientId) throws UnknownHostException, SocketException {
        this.hostName = hostName;
        this.portNumber = portNumber;
        hostAddress = InetAddress.getByName(hostName);
        serverSocket = new DatagramSocket();
        id = clientId;
        sendId();
    }

    public ClientUdp(String hostName, int portNumber, int clientPort, long clientId) throws UnknownHostException, SocketException {
        this.hostName = hostName;
        this.portNumber = portNumber;
        hostAddress = InetAddress.getByName(hostName);
        serverSocket = new DatagramSocket(clientPort);
        id = clientId;
        sendId();
    }

    @Override
    public String receiveMessage() throws IOException {
        byte [] buffer = new byte[bufferSize];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(receivedPacket);
        return new String(receivedPacket.getData(), 0, receivedPacket.getLength());
    }

    @Override
    public void sendMessage(String message) throws IOException {
        message = "UDP Sender " + String.valueOf(id) + ": " + message;
        byte [] buf = message.getBytes();

        for(int offset = 0; offset < buf.length; offset += bufferSize) {
            int remaingBytesLength = buf.length - offset;
            int length = bufferSize < remaingBytesLength ? bufferSize : remaingBytesLength;
            DatagramPacket datagramPacket = new DatagramPacket(buf, offset, length, hostAddress, portNumber);
            serverSocket.send(datagramPacket);
        }
    }

    @Override
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void close() {
        serverSocket.close();
    }

    private void sendId() {
        try {
            sendMessage(String.valueOf(id));
        } catch (IOException e) {
            System.out.println("Hello message error.");
        }
    }

    @Override
    public long getId() {
        return id;
    }
}
