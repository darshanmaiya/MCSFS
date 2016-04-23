package mcsfs.store;

import mcsfs.Store;
import mcsfs.utils.LogUtils;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AzureStore implements Store {
    
    private static final String LOG_TAG = "AzureStore";
    private static final String ACCOUNT_NAME = "";
    private static final String ACCOUNT_KEY = "";
    private static final String CONTAINER_NAME = "container";

    private CloudBlobContainer container;
        
    public AzureStore() {
        try {
            CloudStorageAccount account = CloudStorageAccount.parse("DefaultEndpointsProtocol=http;" + "AccountName=" + ACCOUNT_NAME + ";" + "AccountKey=" + ACCOUNT_KEY);
            CloudBlobClient serviceClient = account.createCloudBlobClient();
            container = serviceClient.getContainerReference(CONTAINER_NAME);
            container.createIfNotExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a file from Azure, stores it in the local file system and returns its absolute path.
     */
    @Override
    public String retrieve(String str) throws IOException, GeneralSecurityException {
        try {
            for (ListBlobItem blobItem : container.listBlobs()) {
                if (blobItem instanceof CloudBlob) {
                    CloudBlob blob = (CloudBlob) blobItem;
                    if (blob.getName().equals(str)) {
                        blob.download(new FileOutputStream("~/" + str));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "~/" + str;
    }

    /**
     * Uploads a file to Azure.
     */
    @Override
    public void store(File file) throws IOException, GeneralSecurityException {
        try {
            LogUtils.debug(LOG_TAG, "Uploading new file. Name: " + file.getName());
            CloudBlockBlob blob = container.getBlockBlobReference(file.getName());
            blob.upload(new FileInputStream(file), file.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

