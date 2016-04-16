/*
    Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
