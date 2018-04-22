package com.rozproszone.zad1.client.client;

import java.io.IOException;

public interface Client {
    String receiveMessage() throws IOException;

    void sendMessage(String message) throws IOException;

    int getPort();

    void close();

    long getId();
}
