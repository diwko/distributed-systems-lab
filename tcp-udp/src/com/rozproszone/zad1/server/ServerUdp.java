package com.rozproszone.zad1.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ServerUdp {
    private final int portNumber;
    private int bufferSize = 1024;
    private Set<SocketAddress> connectedAdresses = new HashSet<>();
    private DatagramSocket datagramSocket = null;
    private ExecutorService executorService;

    public ServerUdp(int portNumber, ExecutorService executorService) {
        this.portNumber = portNumber;
        this.executorService = executorService;
    }

    public void start() throws SocketException {
        openDatagramSocket();

        executorService.execute(this::broadcastMessagesTask);
    }

    public void close() {
        if(datagramSocket != null) {
            datagramSocket.close();
        }
    }

    private void broadcastMessagesTask() {
        while (true) {
            try {
                DatagramPacket datagramPacket = receiveDatagram();
                broadcastDatagram(datagramPacket);
            } catch (IOException e) {
                System.out.println("Server socket error: " + e.getMessage());
                break;
            }
        }
    }

    private DatagramPacket receiveDatagram() throws IOException {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(datagramPacket);
        System.out.println("UDP datagram received: " + new String(datagramPacket.getData(), 0, datagramPacket.getLength()));
        return datagramPacket;
    }

    private void broadcastDatagram(DatagramPacket datagramPacket) {
        SocketAddress senderAddress = datagramPacket.getSocketAddress();

        //Hello message, add address
        if(!connectedAdresses.contains(senderAddress)) {
            connectedAdresses.add(datagramPacket.getSocketAddress());
            return;
        }

        List<SocketAddress> inactiveAddresses = new LinkedList<>();

        for (SocketAddress addr : connectedAdresses) {
            if (addr.equals(senderAddress))
                continue;

            try {
                datagramPacket.setSocketAddress(addr);
                System.out.println("UDP send: " + new String(datagramPacket.getData(), 0, datagramPacket.getLength()));
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                System.out.println("Innactive address: " + addr);
                inactiveAddresses.add(addr);
            }
        }

        inactiveAddresses.forEach(a -> {
            connectedAdresses.remove(a);
            System.out.println("UDP client disconnected: " + a);
        });
    }

    private void openDatagramSocket() throws SocketException {
        datagramSocket = new DatagramSocket(portNumber);
    }
}
