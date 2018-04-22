package distributed_systems.jgroups;

import distributed_systems.jgroups.testApp.TestApp;

public class Main {

    public static void main(String[] args) throws Exception {
        /*Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for (Handler handler : handlers) {
            globalLogger.removeHandler(handler);
        }*/

        TestApp testApp = new TestApp(args[0]);
        testApp.start();
    }
}
