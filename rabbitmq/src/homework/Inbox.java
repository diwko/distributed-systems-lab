package homework;

import com.rabbitmq.client.*;
import homework.message.ExaminationMessageContent;
import homework.message.Message;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class Inbox {
    private final String HOST = "localhost";
    private final String EXCHANGE_NAME = "hospital";
    private final Channel channel;
    private final String topicQueueName;
    private final Map<InjuryType, String> injuryQueuesNames = new HashMap<>();
    private Consumer consumer;

    public Inbox() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.basicQos(1);

        getInjuryQueues();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        topicQueueName = channel.queueDeclare().getQueue();
    }

    private void getInjuryQueues() {
        Stream.of(InjuryType.values()).forEach(injuryType -> {
            try {
                String queueName = injuryType + "_injury_queue";
                injuryQueuesNames.put(injuryType, queueName);
                channel.queueDeclare(queueName, false, false, false, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Inbox subscribe(String topic) throws IOException {
        channel.queueBind(topicQueueName, EXCHANGE_NAME, topic);
        return this;
    }

    public Inbox setReceiveTopicAction(java.util.function.Consumer<Message> action) throws InterruptedException, IOException {
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Message message = null;
                try {
                    message = (Message) Util.convertFromBytes(body);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                action.accept(message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(topicQueueName, false, consumer);
        return this;
    }

    public Inbox setReceiveInjuryAction(java.util.function.Consumer<Message> action, InjuryType[] injuryTypes) throws InterruptedException, IOException {
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Message message = null;
                try {
                    message = (Message) Util.convertFromBytes(body);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                action.accept(message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        Stream.of(injuryTypes).forEach(type -> {
            try {
                channel.basicConsume(injuryQueuesNames.get(type), false, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return this;
    }

    public void sendExaminationRequest(Message message) throws IOException {
        InjuryType injuryType = ((ExaminationMessageContent)message.getContent()).getInjuryType();

        byte[] byteMessage = Util.convertToBytes(message);
        channel.basicPublish("", injuryQueuesNames.get(injuryType),null, byteMessage);
    }

    public void sendMessage(String topic, Message message) throws IOException {
        byte[] byteMessage = Util.convertToBytes(message);
        channel.basicPublish(EXCHANGE_NAME, topic, null, byteMessage);
    }




}
