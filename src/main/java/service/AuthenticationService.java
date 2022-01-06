package service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class AuthenticationService {

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean passwordIsEqual(String password, String hashedPasswordToCompare) {
        return password.equals(hashedPasswordToCompare);
    }

    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordToHash;
    }
}
