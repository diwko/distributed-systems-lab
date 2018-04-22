package homework.app;

import homework.InjuryType;
import homework.Technician;
import homework.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class TechnicianApp {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Technician technician = new Technician(readSpecializations());
        technician.start();
    }

    private static InjuryType[] readSpecializations() throws IOException {
        System.out.print("Enter specialization separated by comma [elbow, hip, knee]: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] specializations = reader.readLine().replaceAll("\\s","").split(",");
        return Arrays.stream(specializations)
                .map(Util::getInjuryType)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(InjuryType[]::new);
    }

}
