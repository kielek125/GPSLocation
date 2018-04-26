package com.malavero.trackyourchild.gpslocation.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.malavero.trackyourchild.gpslocation.services.AppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class Utils {

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
    public static void generateNoteOnFile(String sBody) {
        FileOutputStream fos ;
        try {
            fos = new FileOutputStream(AppConfig.filePath + "/filename.txt", true);

            FileWriter fWriter;

            try {
                fWriter = new FileWriter(fos.getFD());
                sBody = sBody + " - " + Calendar.getInstance().getTime() + System.getProperty("line.separator");
                fWriter.append(sBody);
                fWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.getFD().sync();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}