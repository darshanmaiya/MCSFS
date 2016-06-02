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

import mcsfs.store.Store;
import mcsfs.store.azure.AzureStore;
import mcsfs.store.gcs.GCSConstants;
import mcsfs.utils.LogUtils;

public class AzureStoreTest {
	private static final String LOG_TAG = "AzureStoreTest";

	public static void main(String[] args) throws Exception {
		Store store = new AzureStore();
		File file = new File("Dockerfile");

		LogUtils.setLogLevel(3);
		store.store(file);
		LogUtils.debug(LOG_TAG, "Upload finished.");

		LogUtils.debug(LOG_TAG, "Attempting to download file from bucket "
				+ GCSConstants.GCS_BUCKET_NAME);
		store.retrieve("Dockerfile");
		LogUtils.debug(LOG_TAG, "Download complete.");
	}

}
