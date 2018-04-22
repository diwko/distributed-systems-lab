package homework;

import homework.message.ExaminationMessageContent;
import homework.message.Message;
import homework.message.MessageType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Technician {
    private final Set<InjuryType> specializations;
    private final Inbox inbox;
    private final String [] observedTopics = {"hospital.info"};
    private final String resultTopic = "hospital.main.examination.done.{patient_name}";

    public Technician(InjuryType[] specializations) throws IOException, TimeoutException {
        this.inbox = new Inbox();
        this.specializations = new HashSet<>(Arrays.asList(specializations));
    }

    public void start() throws IOException, InterruptedException {
        Arrays.asList(observedTopics).forEach(topic -> {
            try {
                inbox.subscribe(topic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        inbox.setReceiveTopicAction(this::receiveAction);
        inbox.setReceiveInjuryAction(
                this::receiveAction,
                specializations.toArray(new InjuryType[specializations.size()]));
    }

    private void receiveAction(Message message){
        System.out.println("\n\nRECEIVED:\n" + message);
        if(message.getType() == MessageType.EXAMINATION_REQUEST)
            try {
                processMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void processMessage(Message message) throws IOException {
        ExaminationMessageContent content = (ExaminationMessageContent)message.getContent();
        Message resultMessage = new Message(
                MessageType.RESULT,
                new ExaminationMessageContent(
                        content.getInjuryType(),
                        content.getPatientName(),
                        ExaminationStatus.DONE));

        String topic = resultTopic.replace("{patient_name}", content.getPatientName());
        inbox.sendMessage(topic, resultMessage);
    }

}
