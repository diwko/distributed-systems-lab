package homework.app;

import homework.Doctor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DoctorApp {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Doctor doctor = new Doctor();
        doctor.start();
    }
}
