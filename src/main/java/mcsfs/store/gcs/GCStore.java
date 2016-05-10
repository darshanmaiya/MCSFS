package mcsfs.store.gcs;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;

import mcsfs.store.Store;
import mcsfs.utils.LogUtils;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GCStore implements Store{

    private static final String LOG_TAG = "GCStore";

    /**
     * Retrieves a file from GCS, stores it in the local file system and returns its absolute path.
     */
    @Override
    public String retrieve(String str) throws IOException, GeneralSecurityException {
        File localCopy = new File(GCSConstants.GCS_DIRECTORY + "/" + str);
        localCopy.getParentFile().mkdirs();
        localCopy.createNewFile();

        Storage client = StorageFactory.getService();
        Storage.Objects.Get bucketRequest = client.objects().get(GCSConstants.GCS_BUCKET_NAME, toGCSFileName(str));

        bucketRequest.getMediaHttpDownloader().setDirectDownloadEnabled(true);
        bucketRequest.executeMediaAndDownloadTo(new FileOutputStream(localCopy));
        LogUtils.debug(LOG_TAG, "Download complete. Path: " + localCopy.getAbsolutePath());
        return localCopy.getAbsolutePath();
    }

    /**
     * Uploads a file to GCS.
     */
    @Override
    public void store(File file) throws IOException, GeneralSecurityException {
        LogUtils.debug(LOG_TAG, "Uploading new file. Name: " + file.getName());
        uploadFile(file.getName(), GCSConstants.FILE_TYPE, file, GCSConstants.GCS_BUCKET_NAME);
    }

    /**
     * Utility function.
     * @param str
     * @return
     */
    private String toGCSFileName(String str) {
        String[] tokens = str.split("/");
        String fileName = tokens[tokens.length - 1];
        LogUtils.debug(LOG_TAG, "Extracted file name: " + fileName);

        return fileName;
    }

    /**
     * Uploads data to an object in a bucket.
     *
     * @param name the name of the destination object.
     * @param contentType the MIME type of the data.
     * @param file the file to upload.
     * @param bucketName the name of the bucket to create the object in.
     */
    private void uploadFile(String name, String contentType, File file, String bucketName) throws IOException,
            GeneralSecurityException {
        InputStreamContent contentStream = new InputStreamContent(contentType, new FileInputStream(file));
        // Setting the length improves upload performance
        contentStream.setLength(file.length());
        StorageObject objectMetadata = new StorageObject()
                // Set the destination object name
                .setName(name)
                // Set the access control list to publicly read-only
                .setAcl(Arrays.asList(
                        new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        // Do the insert
        Storage client = StorageFactory.getService();
        Storage.Objects.Insert insertRequest = client.objects().insert(
                bucketName, objectMetadata, contentStream);

        insertRequest.execute();
        LogUtils.debug(LOG_TAG, "Successfully uploaded file to bucket.\nName: " + name + "\nBucket name: " +
                bucketName);
    }

    /**
     * Fetch a list of the objects within the given bucket.
     * @param bucketName
     * @return a list of the contents of the specified bucket.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static List<StorageObject> listBucket(String bucketName) throws IOException, GeneralSecurityException {
        Storage client = StorageFactory.getService();
        Storage.Objects.List listRequest = client.objects().list(bucketName);

        List<StorageObject> results = new ArrayList<StorageObject>();
        Objects objects;

        // Iterate through each page of results, and add them to our results list.
        do {
            objects = listRequest.execute();
            // Add the items in this page of results to the list we'll return.
            results.addAll(objects.getItems());

            // Get the next page, in the next iteration of this loop.
            listRequest.setPageToken(objects.getNextPageToken());
        } while (null != objects.getNextPageToken());

        return results;
    }

    /**
     * Fetches the metadata for the given bucket.
     *
     * @param bucketName the name of the bucket to get metadata about.
     * @return a Bucket containing the bucket's metadata.
     */
    private static Bucket getBucket(String bucketName) throws IOException, GeneralSecurityException {
        Storage client = StorageFactory.getService();

        Storage.Buckets.Get bucketRequest = client.buckets().get(bucketName);
        // Fetch the full set of the bucket's properties (e.g. include the ACLs in the response)
        bucketRequest.setProjection("full");
        return bucketRequest.execute();
    }

    /**
     * Deletes an object in a bucket.
     *
     * @param path the path to the object to delete.
     * @param bucketName the bucket the object is contained in.
     */
    private void deleteObject(String path, String bucketName)
            throws IOException, GeneralSecurityException {
        Storage client = StorageFactory.getService();
        client.objects().delete(bucketName, path).execute();
    }

	@Override
	public void remove(String accessKey) throws Exception{
		// TODO Auto-generated method stub
        LogUtils.debug(LOG_TAG, "Deleting file. Name: " + accessKey);
        deleteObject(accessKey, GCSConstants.GCS_BUCKET_NAME);
        LogUtils.debug(LOG_TAG, "Deleting key. Name: " + accessKey + "_key");
        deleteObject(accessKey + "_key", GCSConstants.GCS_BUCKET_NAME);
    }
}
