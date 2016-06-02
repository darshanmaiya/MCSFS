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

import mcsfs.store.Store;
import mcsfs.store.gcs.GCSConstants;
import mcsfs.store.gcs.GCStore;
import mcsfs.utils.LogUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class GCStoreTest {
    private static final String LOG_TAG = "GCStoreTest";
    private static final String fileName = "this_is_a_test5.txt";
    private static final String directory = "/Users/aviral/Desktop/";

    public static void main(String[] args) throws Exception{
        Store store = new GCStore();
        File file = new File(directory + fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();

        Files.write(file.toPath(), Arrays.asList("This is a TEST5."), Charset.forName("UTF-8"));

        LogUtils.setLogLevel(3);
        LogUtils.debug(LOG_TAG, "File contents: " + new Scanner(file).nextLine());
        LogUtils.debug(LOG_TAG, "Attempting to store file to bucket " + GCSConstants.GCS_BUCKET_NAME);
        store.store(file);
        LogUtils.debug(LOG_TAG, "Upload finished.");
        file.delete();

        LogUtils.debug(LOG_TAG, "Attempting to download file from bucket " + GCSConstants.GCS_BUCKET_NAME);
        store.retrieve(fileName);
        LogUtils.debug(LOG_TAG, "Download complete.");
    }


}
