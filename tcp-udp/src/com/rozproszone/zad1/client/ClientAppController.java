package com.rozproszone.zad1.client;

import com.rozproszone.zad1.client.client.Client;
import com.rozproszone.zad1.client.client.ClientMulticast;
import com.rozproszone.zad1.client.client.ClientTcp;
import com.rozproszone.zad1.client.client.ClientUdp;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ClientAppController {
    @FXML private TextArea messagesArea;
    @FXML private TextArea newMessageArea;
    @FXML private ChoiceBox<String> modeBox;
    @FXML private CheckBox loopBox;
    @FXML private Slider intervalSlider;
    @FXML private Label intervalLabel;

    private final int portNumber = 9999;
    private final String hostName = "localhost";
    private final String groupName = "224.1.1.1";
    private long clientId;

    private HashMap<String, Client> clients = new HashMap<>();

    private Client currentSenderClient;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void initialize() throws IOException {
        clients.put("TCP", new ClientTcp(hostName, portNumber));
        clientId = clients.get("TCP").getId();
        clients.put("UDP", new ClientUdp(hostName, portNumber, clients.get("TCP").getPort(), clientId));
        clients.put("Multicast", new ClientMulticast(portNumber - 1, groupName, clientId));

        modeBox.setItems(FXCollections.observableArrayList("TCP", "UDP", "Multicast"));
        modeBox.getSelectionModel().selectFirst();
        currentSenderClient = clients.get("TCP");

        modeBox.getSelectionModel().selectedIndexProperty().addListener((val, val1, val2) ->
            setCurrentSenderClient((int) val2)
        );

        newMessageArea.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                sendMessageAction();
            }
        });

        newMessageArea.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && !loopBox.isSelected())  {
                newMessageArea.clear();
            }
        });

        intervalSlider.valueProperty().addListener((v, v1, v2) ->
                intervalLabel.setText(String.valueOf(v2.intValue()) + " ms."));

        startReceivingThreads();
    }

    private void setCurrentSenderClient(int index) {
        synchronized (currentSenderClient) {
            currentSenderClient = clients.get(modeBox.getItems().get(index));
        }
    }

    private void startReceivingThreads() {
        clients.values().forEach(this::startReceivingThread);
    }

    private void startReceivingThread(Client client) {
        executorService.execute(() -> {
            try {
                while(true) {
                    String msg = client.receiveMessage();
                    synchronized (messagesArea) {
                        messagesArea.appendText(msg + "\n");
                    }
                }
            } catch (IOException e) {
                //Socket closed
            }
        });
    }

    private void sendMessageAction() {
        if(loopBox.isSelected())
            executorService.execute(this::sendLoopedMessage);
        else
            sendMessage();
    }

    private void sendLoopedMessage() {
            while (loopBox.isSelected()) {
                sendMessage();
                try {
                    Thread.sleep((int) intervalSlider.getValue());
                } catch (InterruptedException e) {
                    break;
                }
            }
    }

    private void sendMessage() {
        try {
            synchronized (currentSenderClient) {
                currentSenderClient.sendMessage(newMessageArea.getText());
            }
        } catch (IOException e) {
            System.out.println("Cannot send message");
        }
    }

    public void exit() {
        executorService.shutdownNow();
        clients.forEach((k, v) -> v.close());
    }
}
