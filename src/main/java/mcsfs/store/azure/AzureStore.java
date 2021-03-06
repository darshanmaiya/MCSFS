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
		FileReader fileReader = new FileReader("/secret/azure.key");
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
		String localFileName = Constants.AZURE_DIRECTORY + "/" + str + System.currentTimeMillis();
		File localCopy = new File(localFileName);
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
		return localFileName;
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
	 * Delete a file on Azure storage.
	 */
	@Override
	public void remove(String str) throws Exception {
		CloudBlockBlob blobSource = container.getBlockBlobReference(str);
		blobSource.deleteIfExists();
		
		CloudBlockBlob blobSourceKey = container.getBlockBlobReference(str + "_key");
		blobSourceKey.deleteIfExists();
	}

}
