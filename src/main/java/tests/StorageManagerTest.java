/*
    Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tests;


import mcsfs.Constants;
import mcsfs.store.StorageManager;
import mcsfs.utils.LogUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class StorageManagerTest {

    private static final String LOG_TAG = "StorageManagerTest";

    public static void main(String[] args) throws Exception {
        LogUtils.setLogLevel(3);
        StorageManager storageManager = new StorageManager();


        // Create dummy file to store.
        File file = new File(Constants.MCSFS_WORKING_DIR + "TEST.txt");
        file.createNewFile();
        Files.write(file.toPath(), Arrays.asList("This is a dummy file."), Charset.forName("UTF-8"));

        // Store file in the cloud.
        storageManager.storeFile(file);
        LogUtils.debug(LOG_TAG, "Call to store complete.");

        // Retrieve file from the cloud.
        storageManager.retrieveFile(file.getName());
        LogUtils.debug(LOG_TAG, "Call to retrieve complete.");

        // Store key in the cloud.
        storageManager.storeKey(file.getName(), "SECRET_KEY");
        LogUtils.debug(LOG_TAG, "Call to store complete.");

        // Retrieve key from the cloud.
        try{
            storageManager.retrieveKey(file.getName());
        }
        catch(Exception e){
            LogUtils.debug(LOG_TAG, "Key retrieval exception.", e);
        }
        LogUtils.debug(LOG_TAG, "Call to retrieve complete.");

        file.deleteOnExit();
        return;
    }

}
