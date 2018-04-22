package com.rozproszone.zad1.server;

public class Message {
    private long senderId;
    private String content;

    public Message(long senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    public long getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Sender " + senderId + ":\t" + content;
    }
}
