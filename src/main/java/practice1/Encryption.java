package practice1;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Encryption {
    private static Cipher cipher;
    private static SecretKey secretKey;

    static {
        try {
            cipher = Cipher.getInstance("DESede");
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
            keyGenerator.init(168);
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }


    public static byte[] doCrypto(byte[] bytes) throws Exception {
        return encrypt(bytes, secretKey);
    }

    public static byte[] doUnCrypto(byte[] bytes) throws Exception {
        return decrypt(bytes, secretKey);
    }

    static byte[] encrypt(byte[] plainTextByte, SecretKey secretKey)
            throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainTextByte);
        return encryptedBytes;
    }

    static byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey)
            throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return decryptedBytes;
    }
}