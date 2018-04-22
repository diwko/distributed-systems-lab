package homework;

import homework.message.ExaminationMessageContent;
import homework.message.Message;
import homework.message.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Doctor {
    private final Inbox inbox;
    private final String [] observedTopics = {"hospital.info"};
    private final String resultTopicTemplate = "hospital.main.examination.done.{patient_name}";
    private final String examinationRequestTopicTemplate = "hospital.main.examination.to_do.{injury_type}";

    public Doctor() throws IOException, TimeoutException {
        this.inbox = new Inbox();
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
        String patientName = ((ExaminationMessageContent)message.getContent()).getPatientName();
        String injuryType = ((ExaminationMessageContent)message.getContent()).getInjuryType().toString().toLowerCase();

        String topic = examinationRequestTopicTemplate.replace("{injury_type}", injuryType);
        inbox.sendMessage(topic, message);
        inbox.sendExaminationRequest(message);

        String subscribeTopic = resultTopicTemplate.replace("{patient_name}", patientName);
        inbox.subscribe(subscribeTopic);
    }

    private Message getMessageFromConsole() throws IOException {
        InjuryType injuryType = getInjuryTypeFromConsole();
        String patientName = getAttributeFromConsole("Patient name");
        return new Message(
                MessageType.EXAMINATION_REQUEST,
                new ExaminationMessageContent(injuryType, patientName, ExaminationStatus.TO_DO));
    }

    private InjuryType getInjuryTypeFromConsole() throws IOException {
        Optional<InjuryType> injuryType = Optional.empty();
        while (!injuryType.isPresent()) {
            injuryType = Util.getInjuryType(getAttributeFromConsole("Injury [elbow, hip, knee]"));
        }
        return injuryType.get();
    }

    private String getAttributeFromConsole(String attributeName) throws IOException {
        System.out.print(attributeName + ": ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine().trim();
    }

}
