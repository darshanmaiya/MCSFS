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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

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
import mcsfs.utils.LogUtils;
import mcsfs.utils.ThreadUtils;

public class StorageManager {

	private static final String LOG_TAG = "StorageManager";

	private static Store[] fsStore = {new FSStore(1), new FSStore(2), new FSStore(3)};
	
	private static Store[] cloudStore = {new GCStore(), new S3Store(), new S3Store()};
	
	public void storeKey(String accessKey, String keyToStore)
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
		Semaphore semaphore = new Semaphore(3);
		List<File> temp; // Garbage collect these files once the threads return.

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
				keyPart.delete();
        	}
			else{
				// Write to cloud provider.
				ThreadUtils.startThreadWithName(new FilePoster(cloudStore[shareIndex-1], keyPart, semaphore),
						cloudStore[shareIndex-1].getClass().toString());
				temp.add(keyPart);
			}
        }

		if(!Constants.DEPLOY_ON_FILE_SYSTEM){
			// Wait till all threads return.
			while(semaphore.availablePermits() != 3)
				ThreadUtils.sleepQuietly(Constants.EXPECTED_WRITE_LATENCY);

			LogUtils.debug(LOG_TAG, "Write to all three providers successful.");
			for(File file : temp)
				file.delete();
		}
	}
	
	public String retrieveKey(String accessKey)
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
			// Retrieve key from providers simultaneously. Two replies are enough.
			ConcurrentMap<String, File> map = new ConcurrentHashMap<>();

			ThreadUtils.startThreadWithName(new FileRetriever(new GCStore(), accessKey + "_key", map),
					"GCStore");
			ThreadUtils.startThreadWithName(new FileRetriever(new S3Store(), accessKey + "_key", map),
					"S3Store");
			ThreadUtils.startThreadWithName(new FileRetriever(new AzureStore(), accessKey + "_key", map),
					"AzureStore");

			while(map.size() < 2)
				ThreadUtils.sleepQuietly(Constants.EXPECTED_READ_LATENCY);

			LogUtils.debug(LOG_TAG, "At least two files successfully read. Size of map: " + map.size());

			// At least two threads have returned. Populate args as above.
			int i = 1;
			for(Map.Entry<String, File> entry : map.entrySet()){
				args[i * 2] = "-s" + i;
				args[i * 2 + 1] = new Scanner(entry.getValue()).nextLine();
				i++;
				if(i == 3) break;
			}
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
	
	public void storeFile(File fileToStore)
			throws Exception {
		
		// Store file in all data stores
		if(Constants.DEPLOY_ON_FILE_SYSTEM) {
			for (int i = 0; i < 3; i++)
				fsStore[i].store(fileToStore);
			return;
		}

		Semaphore semaphore = new Semaphore(3);
		ThreadUtils.startThreadWithName(new FilePoster(new GCStore(), fileToStore, semaphore), "GCStore");
		ThreadUtils.startThreadWithName(new FilePoster(new S3Store(), fileToStore, semaphore), "S3Store");
		ThreadUtils.startThreadWithName(new FilePoster(new AzureStore(), fileToStore, semaphore), "AzureStore");

		// Wait till all threads return.
		while(semaphore.availablePermits() != 3)
			ThreadUtils.sleepQuietly(Constants.EXPECTED_WRITE_LATENCY);

		LogUtils.debug(LOG_TAG, "Write to all three providers successful.");
	}

	/**
	 * Retrieves file from a provider at random.
	 * @param accessKey
	 * @return File corresponding to access key.
	 * @throws Exception
     */
	public static File retrieveFile(String accessKey) 
			throws Exception {

		if(Constants.DEPLOY_ON_FILE_SYSTEM)
			return new File(fsStore[new Random().nextInt() % 3].retrieve(accessKey));

		// Fault tolerant read from cloud.
		int i = Constants.READ_ATTEMPTS;
		while(i > 0) {
			Callable callable = () -> {
				Store store = cloudStore[new Random().nextInt() % 3];
				LogUtils.debug(LOG_TAG + " " + "retrieveFile ", "Retrieving file from " + store.getClass());
				try {
					String absPath = store.retrieve(accessKey);
					File file = new File(absPath);
					return file;
				} catch (Exception e) {
					return null;
				}
			};
			File file;
			file = (File) Executors.newSingleThreadExecutor().submit(callable).get(Constants
					.MAX_READ_WAIT_TIME_IN_SECONDS, TimeUnit.SECONDS);

			if (file != null)
				return file;
			i--;
		}
		throw new FileNotFoundException();
	}

	private class FileRetriever implements Runnable {

		Store provider;
		String fileName;
		ConcurrentMap<String, File> map;

		FileRetriever(Store provider, String fileName, ConcurrentMap<String, File> map){
			this.provider = provider;
			this.fileName = fileName;
			this.map = map;
		}

		@Override
		public void run() {
			try{
				map.put(provider.getClass().toString(), new File(provider.retrieve(fileName)));
			}
			catch(Exception e){
				LogUtils.error(LOG_TAG + " " + provider.getClass(), "Something went wrong while retrieving file from " +
						"the " + "cloud.", e);
			}
		}
	}

	private class FilePoster implements  Runnable {

		Store provider;
		File file;
		Semaphore semaphore;

		FilePoster(Store provider, File file, Semaphore semaphore){
			this.provider = provider;
			this.file = file;
			this.semaphore = semaphore;
		}

		@Override
		public void run() {
			try{
				semaphore.acquire();
				provider.store(file);
			}
			catch (Exception e){
				LogUtils.error(LOG_TAG + " " + provider.getClass(), "Something went wrong while storing file.", e);
			}
			finally {
				semaphore.release();
				LogUtils.debug(LOG_TAG + " " + provider.getClass(), "Released write semaphore.");
			}
		}
	}
}
