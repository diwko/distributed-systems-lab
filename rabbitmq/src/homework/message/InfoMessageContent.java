package homework.message;

public class InfoMessageContent implements MessageContent {
    private String content;

    public InfoMessageContent() {}

    public InfoMessageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
