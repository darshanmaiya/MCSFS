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
package mcsfs.utils;

import java.util.concurrent.Callable;

/**
 * Created by aviral on 4/23/16.
 */
public class ThreadUtils {

    private static final String LOG_TAG = "ThreadUtils";

    public static Thread startThreadWithName(Runnable runnable, String name) {
        Thread thread = new Thread(runnable);
        thread.setName(name);
        thread.start();
        return thread;
    }

    public static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            LogUtils.error(LOG_TAG, "Failed to sleep", e);
        }
    }
}
