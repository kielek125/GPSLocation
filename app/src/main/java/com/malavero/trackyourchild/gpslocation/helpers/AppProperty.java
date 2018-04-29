package com.malavero.trackyourchild.gpslocation.helpers;

import android.os.Environment;

/**
 * Created by Kiełson on 29.04.2018.
 */

public class AppProperty {
    public final static int MEMORY_ACCESS = 5;
    public final static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Logs";
    public final static String fileName = "/ExceptionsLogs.txt";
}
