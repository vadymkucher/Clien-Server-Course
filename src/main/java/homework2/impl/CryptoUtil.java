package homework2.impl;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private static final int KEY_SIZE = 16;

	public static byte[] encrypt(String key, byte[] input) throws CryptoException {

		return doCrypto(Cipher.ENCRYPT_MODE, key, input);
	}

	public static byte[] decrypt(String key, byte[] input) throws CryptoException {
		return doCrypto(Cipher.DECRYPT_MODE, key, input);
	}

	private static byte[] doCrypto(int cipherMode, String key, byte[] input) throws CryptoException {
		try {

			if (key.length() != KEY_SIZE) {
				throw new CryptoException("Wrong key size! Required key of 16 symbols.");
			}

			Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);

			return cipher.doFinal(input);

		} catch (NoSuchPaddingException
				| NoSuchAlgorithmException
				| InvalidKeyException
				| BadPaddingException
				| IllegalBlockSizeException ex) {
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}
}