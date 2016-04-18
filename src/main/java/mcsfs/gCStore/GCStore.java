package mcsfs.gcStore;

import com.google.appengine.tools.cloudstorage.*;
import mcsfs.Store;
import mcsfs.utils.LogUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;

public class GCStore implements Store{

    private static final String LOG_TAG = "GCStore";

    /**
     * This is where back-off parameters are configured. Here it is aggressively retrying with
     * back-off, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private GcsService gcsService;
    /**
     * Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB
     */
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    public GCStore(){
        gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
                .initialRetryDelayMillis(10)
                .retryMaxAttempts(10)
                .totalRetryPeriodMillis(15000)
                .build());
        LogUtils.debug(LOG_TAG, "Successfully created GCS service object.");
    }


    /**
     * Retrieves a file from GCS, stores it in the local file system and returns its path.
     * If the actual file path in GCS is /gcs/Foo/Bar (i.e. the GCS file named Bar in the bucket Foo), the expected
     * str value is Bar.
     */

    @Override
    public String retrieve(String str) throws IOException {

        File localCopy = new File(GCSConstants.GCS_DIRECTORY + str);

        if(!localCopy.exists()) {
            GcsFilename fileName = toGCSFileName(str);
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
            LogUtils.debug(LOG_TAG, "Successfully initialised GCS read channel.");
            copyStream(Channels.newInputStream(readChannel), new FileOutputStream(localCopy));
        }

        return localCopy.getAbsolutePath();
    }

    private GcsFilename toGCSFileName(String str) {
        String[] tokens = str.split("/");
        String fileName = tokens[tokens.length - 1];
        LogUtils.debug(LOG_TAG, "Extracted file name: " + fileName);

        return new GcsFilename(GCSConstants.GCS_BUCKET_NAME, fileName);
    }

    @Override
    public void store(File file) throws IOException {
        GcsOutputChannel outputChannel =
                gcsService.createOrReplace(toGCSFileName(file.getAbsolutePath()), GcsFileOptions.getDefaultInstance());
        LogUtils.debug(LOG_TAG, "Successfully initialised GCS write channel.");
        copyStream(new FileInputStream(file), Channels.newOutputStream(outputChannel));

        // Delete local copy.
        Files.delete(file.toPath());
        LogUtils.debug(LOG_TAG, "Local copy of file deleted.");
    }

    private void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = inputStream.read(buffer);
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesRead = inputStream.read(buffer);
            }
        } finally {
            inputStream.close();
            outputStream.close();
            LogUtils.debug(LOG_TAG, "Stream successful.");
        }
    }
}