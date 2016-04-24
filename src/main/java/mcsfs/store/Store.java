package mcsfs.store;

import java.io.File;

public interface Store {
    /**
     * Retrieve file from storage location to the local file system.
     * @param str
     * @return Absolute path of the file in the local filesystem.
     */
    public String retrieve(String str) throws Exception;

    /**
     * Store file to storage location.
     * @param file
     */
    public void store(File file) throws Exception;
    
    /**
     * Remove file from storage
     * @param accessKey
     * @throws Exception
     */
    public void remove(String accessKey) throws Exception;
}
