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
package mcsfs.store.fs;

import java.io.File;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.*;

import mcsfs.Constants;
import mcsfs.store.Store;

public class FSStore implements Store {
	
	private String storeDir = Constants.MCSFS_FILES_DIR + Constants.MCSFS_FILES_STORE_DIR;
			
	public FSStore(int id) {
		storeDir = storeDir.replace("{0}", String.valueOf(id));
	}

	@Override
	public String retrieve(String str) throws Exception {
		return storeDir + str;
	}

	@Override
	public void store(File file) throws Exception {
		File destination = new File(storeDir + file.getName());
		destination.createNewFile();
		Files.copy(file.toPath(), destination.toPath(), REPLACE_EXISTING);
	}

	//@Override
	public void remove(String accessKey) {
		new File(accessKey).delete();
	}

}
