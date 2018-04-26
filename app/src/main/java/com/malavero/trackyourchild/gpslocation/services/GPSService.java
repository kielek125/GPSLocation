package com.malavero.trackyourchild.gpslocation.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.malavero.trackyourchild.gpslocation.helpers.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kiełson on 19.04.2018.
 */

public class GPSService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private boolean mRunning;
    private SessionManager session;
    private String token;
    private String TAG = "GPS_TAG";
    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GPSLogs";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate()
    {
        try {
            mRunning = false;
            listener = new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    Log.i(TAG,"Location changed");
                    sendCoordinates(location);
                    Intent i = new Intent("location_update");
                    i.putExtra("Coordinates",location.getLongitude()+" "+location.getLatitude()+" "+location.getAltitude());
                    sendBroadcast(i);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle)
                {
                    Log.i(TAG,s+String.valueOf(i));
                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {
                    Intent settingsPanel = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    settingsPanel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(settingsPanel);
                }
            };
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
        } catch (Exception e) {
            generateNoteOnFile(e.getMessage());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try {
            Log.e(TAG, "onStartCommand");
            Log.e(TAG, "onCreate");
            if (!mRunning)
            {
                mRunning = true;
            }
            session = new SessionManager(getApplicationContext());
            token = session.getToken();

            super.onStartCommand(intent, flags, startId);
            return START_STICKY;
        } catch (Exception e) {
            generateNoteOnFile(e.getMessage());
            return START_NOT_STICKY;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null)
            locationManager.removeUpdates(listener);
    }
    private void sendCoordinates(final Location location) {
        try {
            String tag_string_req = "req_login";

            StringRequest stringRequest = new StringRequest (Request.Method.PUT, AppConfig.URL_UPDATE, new Response.Listener<String>()
            {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.has("error");

                        if (!error) {
                            Log.i(TAG,"data sent successfully");
    //                        Log.i(TAG,jObj.get("Authorization").toString());
                            //     token = jObj.get("Authorization").toString();
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    String body;
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    Log.i(TAG,"Error in sender"+ statusCode);
                    if(error.networkResponse.data!=null) {
                        try
                        {
                            body = new String(error.networkResponse.data,"UTF-8");
                            Log.i(TAG,body);
                            JSONObject jObj = new JSONObject(body);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    Log.i(TAG,""+token);
                    if(token != null)
                        params.put("Authorization", token);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("longitude", String.valueOf(location.getLongitude()));
                    params.put("latitude", String.valueOf(location.getLatitude()));
                    params.put("device_name", Build.ID);
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
        } catch (Exception e) {
            generateNoteOnFile(e.getMessage());
        }
    }
    public void generateNoteOnFile(String sBody) {
        try {
            File root = new File(filePath);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(filePath + "/GPSLocationLogsException.txt");
            FileWriter writer = new FileWriter(gpxfile);
            sBody = sBody + " - " + Calendar.getInstance().getTime() + System.getProperty("line.separator");
            writer.append(sBody);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
