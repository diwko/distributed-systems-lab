package distributed_systems.jgroups.testApp;

import distributed_systems.jgroups.map.DistributedStringMap;
import distributed_systems.jgroups.map.SimpleStringMap;
import org.jgroups.ReceiverAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestApp extends ReceiverAdapter {
    private BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
    private SimpleStringMap map;

    public TestApp(String address) throws Exception {
        map = new DistributedStringMap(address);
    }

    public void start() throws IOException {
        while (true) {
            try {
                mainAction();
                System.out.println("Current map state: " + map.toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void mainAction() throws IOException {
        String action = readInput("> ").toLowerCase();

        switch (action) {
            case "contains":
                containsAction();
                break;
            case "get":
                getAction();

                break;
            case "put":
                putAction();
                break;
            case "remove":
                removeAction();
                break;
            default:
                System.out.println("Unsupported action");
                break;
        }
    }

    private void removeAction() throws IOException {
        String key = readInput("... key: ");
        System.out.println("Remove result: " + map.remove(key));
    }

    private void getAction() throws IOException {
        String key = readInput("... key: ");
        System.out.println("Get result: " + map.get(key));
    }

    private void containsAction() throws IOException {
        String key = readInput("... key: ");
        System.out.println("Contains result: " + map.containsKey(key));
    }

    private void putAction() throws IOException {
        String key = readInput("... key: ");
        String value = readInput("... value: ");
        map.put(key, value);
        System.out.println("Put: {" + key + ", " + value + "}");
    }

    private String readInput(String prompt) throws IOException {
        System.out.print(prompt);
        System.out.flush();
        return inputReader.readLine();
    }
}
