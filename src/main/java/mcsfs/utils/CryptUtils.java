package mcsfs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import mcsfs.Constants;

public class CryptUtils {
	
	public static byte[] messageDigest(String message) 
			throws Exception {
		byte[] digest = null;
		
		digest = message.getBytes(Constants.KIND_UTF8);
		MessageDigest sha = MessageDigest.getInstance(Constants.KIND_SHA256);
			
		digest = sha.digest(digest);
		
		return digest;
	}

	public static void encrypt(byte[] key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public static void decrypt(byte[] key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    public static void doCrypto(int cipherMode, byte[] key, File inputFile, File outputFile)
    		throws Exception {
    	
        Key secretKey = new SecretKeySpec(key, Constants.KIND_AES);
        Cipher cipher = Cipher.getInstance(Constants.KIND_AES);
        cipher.init(cipherMode, secretKey);
        
        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);
        
        byte[] outputBytes = cipher.doFinal(inputBytes);
        
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);
        
        inputStream.close();
        outputStream.close();
    }
    
    public static void getKeys(String fileName, byte[] secretKey, byte[] accessKey) 
    		throws Exception {
    	byte[] key = CryptUtils.messageDigest(fileName);
    	
    	System.arraycopy(Arrays.copyOfRange(key, Constants.ACCESS_KEY_LENGTH, key.length), 0,
    			secretKey, 0, Constants.ACCESS_KEY_LENGTH);
    	
    	System.arraycopy(Arrays.copyOf(key, Constants.ACCESS_KEY_LENGTH), 0,
    			accessKey, 0, Constants.ACCESS_KEY_LENGTH);
    }
}
