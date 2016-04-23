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
package mcsfs.store;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.engine.SecretShare.SplitSecretOutput;
import com.tiemens.secretshare.main.cli.MainCombine.CombineInput;
import com.tiemens.secretshare.main.cli.MainCombine.CombineOutput;
import com.tiemens.secretshare.main.cli.MainSplit.SplitInput;
import com.tiemens.secretshare.main.cli.MainSplit.SplitOutput;
import com.tiemens.secretshare.math.BigIntUtilities;

import mcsfs.Constants;
import mcsfs.store.azure.AzureStore;
import mcsfs.store.fs.FSStore;
import mcsfs.store.gcs.GCStore;
import mcsfs.store.s3.S3Store;

public class StorageManager {
	
	private static Store[] fsStore = {new FSStore(1), new FSStore(2), new FSStore(3)};
	
	private static Store[] cloudStore = {new GCStore(), new S3Store(), new S3Store()};
	
	public static void storeKey(String accessKey, String keyToStore) 
		throws Exception {
		// Split the key with (k=2, n=3) using Adi Shamir Secret Sharing Algorithm
        String[] args = new String[] {
        		Constants.QUORUM_SWITCH, Constants.QUORUM_VALUE,
        		Constants.NUM_SPLITS_SWITCH, Constants.NUMBER_OF_SPLITS,
        		Constants.FILE_NAME_SWITCH, keyToStore
        	};
        
        SplitInput input = SplitInput.parse(args);
        SplitOutput output = input.output();
        
        Field splitSecretOutputField = SplitOutput.class.getDeclaredField("splitSecretOutput");
        splitSecretOutputField.setAccessible(true);
        
        SplitSecretOutput splitSecretOutput = (SplitSecretOutput) splitSecretOutputField.get(output);
        List<SecretShare.ShareInfo> shares = splitSecretOutput.getShareInfos();
        
        // Write the keys to all the data stores
        for (SecretShare.ShareInfo share : shares)
        {
        	int shareIndex = share.getIndex();
        	BigInteger splitShare = share.getShare();

        	File keyPart = new File(Constants.MCSFS_WORKING_DIR + accessKey + "_key");
            keyPart.createNewFile();
			
            PrintWriter writer = new PrintWriter(keyPart);
            writer.println(splitShare);
            writer.close();
            
            // Only write to the file system if testing locally
        	if(Constants.DEPLOY_ON_FILE_SYSTEM) {
	            fsStore[shareIndex-1].store(keyPart);
        	}
        	
        	// Write to rest of data stores here
        	cloudStore[shareIndex-1].store(keyPart);
        	
        	keyPart.delete();
        }
	}
	
	public static String retrieveKey(String accessKey) 
			throws Exception {
		String retrievedKey = null;
		
		// Retrieve QUORUM number of parts of the split key and extract the key back
		String[] args = new String[]{
        		Constants.QUORUM_SWITCH, Constants.QUORUM_VALUE,
        		"", "",
        		"", ""
        	};
		
		Scanner in = null;
		
		if(Constants.DEPLOY_ON_FILE_SYSTEM) {
			int numSplits = Integer.parseInt(Constants.NUMBER_OF_SPLITS);
			// Reading only two for now
			for(int i=1; i<numSplits; i++) {
				
				File keyPart = new File(fsStore[i-1].retrieve(accessKey + "_key"));
	            
	            in = new Scanner(keyPart);
				String contents;
				contents = in.nextLine();
	            args[i*2] = "-s" + i;
	            args[i*2 + 1] = contents;
	            
	            in.close();
			}
		} else {
			// Read from data stores until two replies are got and make note of IDs from where
			// replies have come
		}
		
		// Combine the fields to get the filename
		CombineInput input = CombineInput.parse(args, null, null);
        CombineOutput output = input.output();
        
        Field secretField = CombineOutput.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        
        BigInteger secret = (BigInteger) secretField.get(output);
        
        // Retrieve file name and user file name
        retrievedKey = BigIntUtilities.Human.createHumanString(secret);
		
		return retrievedKey;
	}
	
	public static void remove(String accessKey) 
			throws Exception {
		// Remove the file with name accessKey from all data stores
		
	}
	
	public static void storeFile(File fileToStore) 
			throws Exception {
		
		// Store file in all data stores
		for(int i=0; i<3; i++) {
			if(Constants.DEPLOY_ON_FILE_SYSTEM) {
				fsStore[i].store(fileToStore);
			}
			cloudStore[i].store(fileToStore);
		}
	}
	
	public static File retrieveFile(String accessKey) 
			throws Exception {
		File retrievedFile = null;
		
		retrievedFile = new File(fsStore[((int)Math.random()%3)].retrieve(accessKey));
		
		return retrievedFile;
	}
}
