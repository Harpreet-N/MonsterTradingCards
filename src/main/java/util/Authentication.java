package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Authentication {

    public static boolean passwordIsEqual(String password, String hashedPasswordToCompare) {
        return password.equals(hashedPasswordToCompare);
    }

    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordToHash;
    }


    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
