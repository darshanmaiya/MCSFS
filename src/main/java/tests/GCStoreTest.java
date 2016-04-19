package tests;

import com.google.api.services.storage.Storage;
import mcsfs.Store;
import mcsfs.gcStore.GCSConstants;
import mcsfs.gcStore.GCStore;
import mcsfs.utils.LogUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Scanner;

public class GCStoreTest {
    private static final String LOG_TAG = "GCStoreTest";
    private static final String fileName = "this_is_a_test5.txt";
    private static final String directory = "/Users/aviral/Desktop/";

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        Store store = new GCStore();
        File file = new File(directory + fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();

        Files.write(file.toPath(), Arrays.asList("This is a TEST5."), Charset.forName("UTF-8"));

        LogUtils.setLogLevel(3);
        LogUtils.debug(LOG_TAG, "File contents: " + new Scanner(file).nextLine());
        LogUtils.debug(LOG_TAG, "Attempting to store file to bucket " + GCSConstants.GCS_BUCKET_NAME);
        store.store(file);
        LogUtils.debug(LOG_TAG, "Upload finished.");
        file.delete();

        LogUtils.debug(LOG_TAG, "Attempting to download file from bucket " + GCSConstants.GCS_BUCKET_NAME);
        store.retrieve(fileName);
        LogUtils.debug(LOG_TAG, "Download complete.");
    }


}
