package com.malavero.trackyourchild.gpslocation.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by bkieltyka on 27.04.2018.
 */

public class FileManager {

    private final int MEMORY_ACCESS = 5;

    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Logs";

    public void checkPermissionFile(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MEMORY_ACCESS);
        }
    }

    public void saveFile(Context context) {

        createDir(context);
        createFile(context);
    }

    private void createDir(Context context) {
        File folder = new File(path);
        if (!folder.exists())
            try {
                folder.mkdirs();
            } catch (Exception e) {
                Toast.makeText(context, "Nie udało się utworzyć katalogu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
    }

    private void createFile(Context context) {
        boolean mode;
        File file = new File(path + "/ExceptionsLogs.txt");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        FileOutputStream fout;
        OutputStreamWriter outWriter;
        try {
            fout = new FileOutputStream(file, true);
            outWriter = new OutputStreamWriter(fout);
            outWriter.append("test");
            outWriter.close();

        } catch (Exception e) {
            Toast.makeText(context, "Nie udało się utworzyć pliku: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
