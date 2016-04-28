package mcsfs.store.azure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

import mcsfs.Constants;
import mcsfs.store.Store;
import mcsfs.utils.LogUtils;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class AzureStore implements Store {

	private static final String LOG_TAG = "AzureStore";

	private CloudBlobContainer container;

	@SuppressWarnings("resource")
	public AzureStore() throws InvalidKeyException, URISyntaxException,
			StorageException, IOException {
		FileReader fileReader = new FileReader("azure.key");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String accountName = bufferedReader.readLine();
		String accountKey = bufferedReader.readLine();
		CloudStorageAccount account = CloudStorageAccount
				.parse("DefaultEndpointsProtocol=http;" + "AccountName="
						+ accountName + ";" + "AccountKey=" + accountKey);
		CloudBlobClient serviceClient = account.createCloudBlobClient();
		container = serviceClient
				.getContainerReference(Constants.AZURE_CONTAINER_NAME);
		container.createIfNotExists();
	}

	/**
	 * Retrieves a file from Azure, stores it in the local file system and
	 * returns its absolute path.
	 * 
	 * @throws StorageException
	 * @throws URISyntaxException
	 */
	@Override
	public String retrieve(String str) throws IOException,
			GeneralSecurityException, StorageException, URISyntaxException {
		File localCopy = new File(Constants.AZURE_DIRECTORY + "/" + str);
		if (!localCopy.exists()) {
			localCopy.getParentFile().mkdirs();
			localCopy.createNewFile();
		}
		// System.out.println(localCopy.getAbsolutePath());
		CloudBlockBlob blobSource = container.getBlockBlobReference(str);
		if (blobSource.exists()) {
			blobSource.download(new FileOutputStream(localCopy));
		}
		LogUtils.debug(LOG_TAG, "File " + str + " was downloaded to "
				+ Constants.AZURE_DIRECTORY + " .");
		return Constants.AZURE_DIRECTORY + "/" + str;
	}

	/**
	 * Uploads a file to Azure.
	 * 
	 * @throws StorageException
	 * @throws URISyntaxException
	 */
	@Override
	public void store(File file) throws IOException, GeneralSecurityException,
			StorageException, URISyntaxException {
		CloudBlockBlob blob = container.getBlockBlobReference(file.getName());
		blob.upload(new FileInputStream(file), file.length());
		LogUtils.debug(LOG_TAG, "File " + file.getName() + " was uploaded.");
	}

	/**
	 * Delete a file on Azure storeage.
	 */
	@Override
	public void remove(String str) throws Exception {
		CloudBlockBlob blobSource = container.getBlockBlobReference(str);
		blobSource.deleteIfExists();
	}

}
