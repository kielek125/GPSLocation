package com.malavero.trackyourchild.gpslocation.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.malavero.trackyourchild.gpslocation.services.AppConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

public class Utils {

    private static String TAG = "GPS_TAG";
    // Delay mechanism

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }
    public static boolean isMyServiceRunning(Class<?> serviceClass, Activity activity){
        ActivityManager manager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    public static void generateNoteOnFile(String sBody,Context c) {
        FileOutputStream outputStream ;
        String filename = "sample.txt";
        String path = AppConfig.filePath;
        try
        {
            File file = new File(c.getFilesDir(),"/sample.txt");
            if(!file.exists())
            {
                FileOutputStream fOut = c.openFileOutput(filename, MODE_PRIVATE);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut,StandardCharsets.UTF_8);
                sBody = Calendar.getInstance().getTime() + System.getProperty("line.separator");
                myOutWriter.write(sBody);
                myOutWriter.close();
            }
            else
            {
                FileOutputStream fOut = c.openFileOutput(filename, MODE_APPEND);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut,StandardCharsets.UTF_8);
                sBody = Calendar.getInstance().getTime() + System.getProperty("line.separator");
                myOutWriter.append(sBody);
                myOutWriter.close();
            }

            Log.i(TAG,"File will be created at "+ AppConfig.filePath+"/"+filename);
        } catch (Exception e) {
            Log.i(TAG,"Failed to create file");
            e.printStackTrace();
        }
    }
}