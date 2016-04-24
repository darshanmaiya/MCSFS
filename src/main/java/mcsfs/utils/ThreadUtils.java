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
