package homework;

import homework.message.ExaminationMessageContent;
import homework.message.InfoMessageContent;
import homework.message.Message;
import homework.message.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class Administrator {
    private final Inbox inbox;
    private final String[] observedTopics = {"hospital.main.#"};
    private final String infoTopic = "hospital.info";

    public Administrator() throws IOException, TimeoutException {
        inbox = new Inbox();
    }

    public void start() throws IOException, InterruptedException {
        Arrays.asList(observedTopics).forEach(topic -> {
            try {
                inbox.subscribe(topic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        inbox.setReceiveTopicAction(this::receiveMessageAction);
        sendMessageAction();
    }

    private void receiveMessageAction(Message message) {
        System.out.println("\n\nRECEIVED:\n" + message);
    }

    private void sendMessageAction() throws IOException {
        while(true) {
            sendMessage();
        }
    }

    private void sendMessage() throws IOException {
        Message message = getMessageFromConsole();
        inbox.sendMessage(infoTopic, message);
    }

    private Message getMessageFromConsole() throws IOException {
        String content = getAttributeFromConsole("Info message: ");
        return new Message(
                MessageType.INFO,
                new InfoMessageContent(content));
    }

    private String getAttributeFromConsole(String attributeName) throws IOException {
        System.out.print(attributeName + ": ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine().trim();
    }
}
