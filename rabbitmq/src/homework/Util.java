package homework;

import java.io.*;
import java.util.Optional;

public class Util {
    public static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    public static Optional<InjuryType> getInjuryType(String injuryType) {
        switch (injuryType.toLowerCase()) {
            case "elbow":
                return Optional.of(InjuryType.ELBOW);
            case "hip":
                return Optional.of(InjuryType.HIP);
            case "knee":
                return Optional.of(InjuryType.KNEE);
            default:
                return Optional.empty();
        }
    }
}
