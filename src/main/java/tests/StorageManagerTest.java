package tests;


import mcsfs.store.StorageManager;
import mcsfs.utils.LogUtils;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

public class StorageManagerTest {

    private static final String LOG_TAG = "StorageManagerTest";

    public static void main(String[] args) throws Exception {
        StorageManager storageManager = new StorageManager();
        LogUtils.setLogLevel(3);

        // Create dummy file to store.
        File file = new File("/Users/aviral/Hogwarts/CS-293B/MCSFS/TEST");
        file.createNewFile();
        Files.write(file.toPath(), Arrays.asList("This is a dummy file."), Charset.forName("UTF-8"));

        // Store file in the cloud.
        storageManager.storeFile(file);
        LogUtils.debug(LOG_TAG, "Call to store complete.");

        // Retrieve file from the cloud.
        storageManager.retrieveFile(file.getName());
        LogUtils.debug(LOG_TAG, "Call to retrieve complete.");

        // Store key in the cloud.
        storageManager.storeKey(file.getName(), "SECRET_KEY");
        LogUtils.debug(LOG_TAG, "Call to store complete.");

        // Retrieve key from the cloud.
        try{
            storageManager.retrieveKey(file.getName());
        }
        catch(Exception e){
            LogUtils.debug(LOG_TAG, "Key retrieval exception.", e);
        }
        LogUtils.debug(LOG_TAG, "Call to retrieve complete.");

        file.deleteOnExit();
    }

}
