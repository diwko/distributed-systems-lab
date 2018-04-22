package com.rozproszone.zad1.client.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClientMulticast implements Client {
    private final int portNumber;
    private final InetAddress group;
    private final MulticastSocket multicastSocket;
    private long id;

    private final int bufferSize = 1024;

    public ClientMulticast(int portNumber, String groupName, long clientId) throws IOException {
        this.portNumber = portNumber;
        group = InetAddress.getByName(groupName);
        multicastSocket = new MulticastSocket(portNumber);
        multicastSocket.joinGroup(group);
        id = clientId;
    }

    @Override
    public String receiveMessage() throws IOException {
        byte [] buffer = new byte[bufferSize];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(receivedPacket);

        return new String(receivedPacket.getData(), 0, receivedPacket.getLength());
    }

    @Override
    public void sendMessage(String message) throws IOException {
        message = "Multicast Sender " + String.valueOf(id) + ": " + message;

        byte [] buf = message.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, group, portNumber);
        multicastSocket.leaveGroup(group);
        multicastSocket.send(datagramPacket);
        multicastSocket.joinGroup(group);
    }

    @Override
    public int getPort() {
        return multicastSocket.getLocalPort();
    }

    @Override
    public void close() {
        multicastSocket.close();
    }

    @Override
    public long getId() {
        return id;
    }
}
