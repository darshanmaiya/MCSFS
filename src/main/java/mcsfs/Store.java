package mcsfs;

import java.io.File;

public interface Store {
    /**
     * Retrieve file from storage location to the local file system.
     * @param str
     * @return Path of the file in the local filesystem.
     */
    public String retrieve(String str);

    /**
     * Store file to storage location.
     * @param file
     * @return Success/Failure
     */
    public boolean store(File file);
}
