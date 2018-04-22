package com.rozproszone.zad1.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    public static int portNumber = 9999;

    public static void main(String [] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        ServerTcp serverTcp = new ServerTcp(portNumber, executorService);
        ServerUdp serverUdp = new ServerUdp(portNumber, executorService);

        try {
            serverTcp.start();
            serverUdp.start();

            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            serverTcp.close();
            serverUdp.close();
        }
    }
}
