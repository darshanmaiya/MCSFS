package mcsfs.store.s3;

import java.io.File;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import mcsfs.Constants;
import mcsfs.store.Store;
import mcsfs.utils.LogUtils;

public class S3Store implements Store {

    private static final String LOG_TAG = "S3Store";
	private static String bucketName = Constants.S3_BUCKET_NAME;

	@Override
	public String retrieve(String file) throws Exception {
		LogUtils.debug(LOG_TAG, "Downloading file: " + file);
        TransferManager tm = new TransferManager(new DefaultAWSCredentialsProviderChain());
        // TransferManager processes all transfers asynchronously, 
        // so this call will return immediately.
        File downloadedFile = new File(Constants.MCSFS_WORKING_DIR + Constants.S3_WORKING_DIR + file);
		downloadedFile.getParentFile().mkdirs();
		downloadedFile.createNewFile();
		Download download = tm.download(bucketName, file, downloadedFile);
        download.waitForCompletion();
        LogUtils.debug(LOG_TAG, "Successfully downloaded file from bucket.\nName: " + file + "\nBucket name: " +
                bucketName);
        tm.shutdownNow();
		return downloadedFile.getAbsolutePath();
	}

	@Override
	public void store(File file) throws Exception {
        
		LogUtils.debug(LOG_TAG, "Uploading new file. Name: " + file.getName());
        TransferManager tm = new TransferManager(new DefaultAWSCredentialsProviderChain());
        // TransferManager processes all transfers asynchronously, 
        // so this call will return immediately.
        Upload upload = tm.upload(bucketName, file.getName(), file);
        upload.waitForCompletion();
        LogUtils.debug(LOG_TAG, "Successfully uploaded file to bucket.\nName: " + file.getName() + "\nBucket name: " +
                bucketName);
        tm.shutdownNow();
	}

	@Override
	public void remove(String accessKey) throws Exception {
		// TODO Auto-generated method stub

	}

}
