package mcsfs;

import java.io.File;
import java.io.IOException;

public interface Store {
    /**
     * Retrieve file from storage location to the local file system.
     * @param str
     * @return Absolute path of the file in the local filesystem.
     */
    public String retrieve(String str) throws IOException;

    /**
     * Store file to storage location and delete local copy. (To keep garbage collection simple.)
     * @param file
     * @return Success/Failure
     */
    public void store(File file) throws IOException;
}
