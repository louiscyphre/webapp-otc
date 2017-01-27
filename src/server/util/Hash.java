package server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by student on 1/13/17.
 */
public class Hash {
    public static String getSha256Hex(String msg) throws NoSuchAlgorithmException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(msg.getBytes(), 0, msg.length());
            return msg = digest.digest().toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
