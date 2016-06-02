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

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

import mcsfs.Constants;
import mcsfs.store.Store;
import mcsfs.store.s3.S3Store;
import mcsfs.utils.LogUtils;

public class S3StoreTest {

    private static final String LOG_TAG = "S3StoreTest";
	private static final String fileName = "this_is_a_test.txt";
    private static final String directory = "mcsfs_working_dir/";
    
	public static void main(String[] args) throws Exception {
		Store s3Store = new S3Store();
		File file = new File(directory + fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();

        Files.write(file.toPath(), Arrays.asList("This is S3 store test."), Charset.forName("UTF-8"));

        LogUtils.setLogLevel(3);
        LogUtils.debug(LOG_TAG, "File contents: " + new Scanner(file).nextLine());
        LogUtils.debug(LOG_TAG, "Attempting to store file to bucket " + Constants.S3_BUCKET_NAME);
        s3Store.store(file);
        LogUtils.debug(LOG_TAG, "Upload finished.");
        file.delete();

        LogUtils.debug(LOG_TAG, "Attempting to download file from bucket " + Constants.S3_BUCKET_NAME);
        s3Store.retrieve(fileName);
        LogUtils.debug(LOG_TAG, "Download complete.");
        
        LogUtils.debug(LOG_TAG, "Attempting to delete file from bucket " + Constants.S3_BUCKET_NAME);
        s3Store.remove(fileName);
        LogUtils.debug(LOG_TAG, "Delete complete.");
	}

}
