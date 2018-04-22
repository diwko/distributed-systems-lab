package homework.app;

import homework.Administrator;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AdministratorApp {
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        Administrator administrator = new Administrator();
        administrator.start();
    }
}
