package mcsfs;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface Store {
    /**
     * Retrieve file from storage location to the local file system.
     * @param str
     * @return Absolute path of the file in the local filesystem.
     */
    public String retrieve(String str) throws IOException, GeneralSecurityException;

    /**
     * Store file to storage location.
     * @param file
     */
    public void store(File file) throws IOException, GeneralSecurityException;
}
