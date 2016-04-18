package mcsfs.gCStore;

import com.google.appengine.tools.cloudstorage.*;
import mcsfs.Store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

public class GCStore implements Store{

    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    /**
     * Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB
     */
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    /**
     * Retrieves a file from GCS, stores it in the local file system and returns its path.
     * If the actual file path in GCS is /gcs/Foo/Bar (i.e. the GCS file named Bar in the bucket Foo), the expected
     * str value is Bar.
     */

    @Override
    public String retrieve(String str) throws IOException {

        File localCopy = new File(Constants.GCS_DIRECTORY + str);

        if(!localCopy.exists()) {
            GcsFilename fileName = toGCSFileName(str);
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
            storeLocally(Channels.newInputStream(readChannel), localCopy);
        }

        return localCopy.getAbsolutePath();
    }

    private boolean storeLocally(InputStream inputStream, File localFile) {
        // TODO
        return false;
    }

    private GcsFilename toGCSFileName(String str) {
        // TODO
        return null;
    }

    @Override
    public boolean store(File file) throws IOException {
        GcsOutputChannel outputChannel =
                gcsService.createOrReplace(toGCSFileName(file.getPath()), GcsFileOptions.getDefaultInstance());
        uploadToBucket(file, Channels.newOutputStream(outputChannel));
        return false;
    }

    private void uploadToBucket(File file, OutputStream outputStream) {
        // TODO
    }
}
