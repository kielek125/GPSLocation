package com.malavero.trackyourchild.gpslocation.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.malavero.trackyourchild.gpslocation.helpers.StringHelper;
import com.malavero.trackyourchild.gpslocation.services.GPSService;
import com.malavero.trackyourchild.gpslocation.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button buttonStart, buttonStop;
    private TextView textView,tv_latitude,tv_longitude,tv_altitude;
    private ToggleButton toggleButton;
    private BroadcastReceiver broadcastReceiver;
    private HttpURLConnection registerConnection;
    private HttpURLConnection loginConnection;
    private HttpURLConnection updateConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttonStart = (Button) findViewById(R.id.startTracingButton);
        //buttonStop = (Button) findViewById(R.id.cancelTrackingButton);
        textView = (TextView) findViewById(R.id.coordinateTextView);

        tv_latitude = (TextView) findViewById(R.id.tv_coordinates_latitude_values);
        tv_longitude = (TextView) findViewById(R.id.tv_coordinates_longitude_values);
        tv_altitude = (TextView) findViewById(R.id.tv_coordinates_altitude_values);

        toggleButton = (ToggleButton) findViewById(R.id.tb_service);

        if(!runtimePermission())
            enableToggleButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            URL registerURL = new URL("http://helpdesk.yorki-dev.com/api/auth/register");
            URL loginURL = new URL("http://geoloc.yorki-dev.com/api/auth/login");
            URL updateURL = new URL("http://geoloc.yorki-dev.com/api/location/update");

            registerConnection = (HttpURLConnection)  registerURL.openConnection();
            loginConnection = (HttpURLConnection)  loginURL.openConnection();
            updateConnection = (HttpURLConnection)  updateURL.openConnection();

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost request = new HttpPost("http://geoloc.yorki-dev.com/api/auth/register");

            request.setHeader( "Content-Type", "application/json" );

            JSONObject json = new JSONObject();
            json.put("name", "kielek");
            json.put("email", "kielek@otoja.pl");
            json.put("password", "over1lord");

            StringEntity se = new StringEntity(json.toString());
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");

            request.setEntity(se);
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String _response = StringHelper.convertStreamToString(is);
            System.out.println("res--  " + _response);

            // Check if server response is valid code
            int res_code = response.getStatusLine().getStatusCode();
            System.out.println("code-- " +res_code);

//            registerConnection.setRequestMethod("POST");
//            String user = "name=kielek&mail=kielek@otoja.pl&password=over1lord";
//            registerConnection.setDoOutput(true);
//            registerConnection.getOutputStream().write(user.getBytes());

//            RestClient c = new RestClient("http/helpdesk.yorki-dev.com/api/auth/register&quot");
//            c.AddHeader("Accept", "application/json");
//            c.AddHeader("Content-type", "application/json");
//            c.AddParam("name", "kielek");
//            c.AddParam("email", "kielek@otoja.pl");
//            c.AddParam("password", "over1lord");
//            c.Execute(RestClient.RequestMethod.POST);
//
//            JSONObject key = new JSONObject(c.getResponse());
//            String error = key.getString("error");
//            String success = key.getString("success");

        }
        catch (Exception ex){
            Toast.makeText(this,ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (broadcastReceiver == null)
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent)
                    {
                        setCoordinatesText(intent.getExtras().get("Coordinates").toString());
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //updateConnection.setRequestProperty("Authorization", "SLUMl6oaMuw6hJGFN0UgIkXMoev2XjS26AloYmtTMLeMK3jkmsEuXjI9YGKScKCl3jVizvuhoKZSdARKJmtyUCR9LVoseG3aG4BGB4WmeDUZt1rtLVwgzfqLel5KmD9T");
                                //if (updateConnection.getResponseCode() == 200) {

                                //}
                            }
                        });
                    }
                };
            registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        }
        catch (Exception ex) {

        }
    }
//
//    private void enableButtons() {
//        buttonStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent service = new Intent(getApplicationContext(), GPSService.class);
//                startService(service);
//            }
//        });
//        buttonStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent service = new Intent(getApplicationContext(), GPSService.class);
//                stopService(service);
//            }
//        });
//    }

    private void setCoordinatesText(String coordinates)
    {
        String[] coords = coordinates.split(" ");
        tv_longitude.setText(coords[0]);
        tv_latitude.setText(coords[1]);
        tv_altitude.setText(coords[2]);
    }
    private void enableToggleButton()
    {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    Intent service = new Intent(getApplicationContext(), GPSService.class);
                    Log.d("service_enabled","GPS LOCALIZATION HAS BEEN ENABLED");
                    startService(service);
                    Toast.makeText(MainActivity.this,getString(R.string.app_service_enable_description),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent service = new Intent(getApplicationContext(), GPSService.class);
                    Log.d("service_disabled","GPS LOCALIZATION HAS BEEN DISABLED");
                    stopService(service);
                    Toast.makeText(MainActivity.this,getString(R.string.app_service_disable_description),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean runtimePermission() {
        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    enableToggleButton();
                else
                    runtimePermission();
                return;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }
}
