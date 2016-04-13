package mcsfs.utils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtils {
	private static Cipher cipher;

	public static String encrypt(String plainText, String secretKey)
			throws Exception {
		
		byte[] key = (secretKey).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); // use only first 128 bits

		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		
		cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		
		byte[] plainTextByte = plainText.getBytes("UTF-8");
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		
		return encryptedText;
	}

	public static String decrypt(String encryptedText, String secretKey)
			throws Exception {
		
		byte[] key = (secretKey).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); // use only first 128 bits

		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte, "UTF-8");
		
		return decryptedText;
	}
}
