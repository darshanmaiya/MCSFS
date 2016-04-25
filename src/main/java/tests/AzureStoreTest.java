package tests;

import java.io.File;

import mcsfs.store.Store;
import mcsfs.store.azure.AzureStore;
import mcsfs.store.gcs.GCSConstants;
import mcsfs.utils.LogUtils;

public class AzureStoreTest {
	private static final String LOG_TAG = "AzureStoreTest";

	public static void main(String[] args) throws Exception {
		Store store = new AzureStore();
		File file = new File("Dockerfile");

		LogUtils.setLogLevel(3);
		store.store(file);
		LogUtils.debug(LOG_TAG, "Upload finished.");

		LogUtils.debug(LOG_TAG, "Attempting to download file from bucket "
				+ GCSConstants.GCS_BUCKET_NAME);
		store.retrieve("Dockerfile");
		LogUtils.debug(LOG_TAG, "Download complete.");
	}

}
