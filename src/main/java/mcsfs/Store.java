package mcsfs;

import java.io.File;
import java.io.IOException;

public interface Store {
    /**
     * Retrieve file from storage location to the local file system.
     * @param str
     * @return Absolute Path of the file in the local filesystem.
     */
    public String retrieve(String str) throws IOException;

    /**
     * Store file to storage location.
     * @param file
     * @return Success/Failure
     */
    public boolean store(File file) throws IOException;
}
