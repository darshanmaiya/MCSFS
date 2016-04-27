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
package mcsfs;

public class Constants {
	public static final String KIND_UTF8 = "UTF-8";
	public static final String KIND_SHA256 = "SHA-256";
	public static final String KIND_AES = "AES";
	public static final int ACCESS_KEY_LENGTH = 16;
	
	public static final int BUFFER_SIZE = 4096;
	
	public static final String DELIMITER_IN_FILENAME = "___";
	public static final String MCSFS_WORKING_DIR = "mcsfs_working_dir/";
	public static final String MCSFS_FILES_DIR = "mcsfs_files/";
	public static final String MCSFS_FILES_STORE_DIR = "store{0}/"; // {0} is replaced with ID
	
	// Options for Secretshare library
	public static final String NUMBER_OF_SPLITS = "3";
	public static final String QUORUM_VALUE = "2";
	public static final String QUORUM_SWITCH = "-k";
	public static final String NUM_SPLITS_SWITCH = "-n";
	public static final String FILE_NAME_SWITCH = "-sS";

	// S3 Constants
	public static final String S3_BUCKET_NAME = "mcsfs-files";

	public static final boolean DEPLOY_ON_FILE_SYSTEM = true;
	public static final int EXPECTED_READ_LATENCY = 10; // In milliseconds.
	public static final int EXPECTED_WRITE_LATENCY = 10; // In milliseconds.
	public static final int READ_ATTEMPTS = 3;
	public static final int MAX_READ_WAIT_TIME_IN_SECONDS = 10;

	public static final boolean TEST_GCS_ONLY = true;
}
