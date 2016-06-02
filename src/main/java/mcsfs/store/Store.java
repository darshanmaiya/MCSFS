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
package mcsfs.store;

import java.io.File;

public interface Store {
    /**
     * Retrieve file from storage location to the local file system.
     * @param str
     * @return Absolute path of the file in the local filesystem.
     */
    public String retrieve(String str) throws Exception;

    /**
     * Store file to storage location.
     * @param file
     */
    public void store(File file) throws Exception;
    
    /**
     * Remove file from storage
     * @param accessKey
     * @throws Exception
     */
    public void remove(String accessKey) throws Exception;
}
