package homework.message;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType type;
    private MessageContent content;

    public Message() {}

    public Message(MessageType type, MessageContent content) {
        this.type = type;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Type: " + type + "\n"
                + "Content: " + content;
    }
}
