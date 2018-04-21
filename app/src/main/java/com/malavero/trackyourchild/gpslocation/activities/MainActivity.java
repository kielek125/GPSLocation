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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.malavero.trackyourchild.gpslocation.R;
import com.malavero.trackyourchild.gpslocation.services.GPSService;
import com.malavero.trackyourchild.gpslocation.utils.RestSender;

public class MainActivity extends AppCompatActivity {

    private TextView textView, tv_latitude, tv_longitude, tv_altitude;
    private ToggleButton toggleButton;
    private BroadcastReceiver broadcastReceiver;
    private RestSender restSender;
    private String login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        restSender = new RestSender(login,password);
        textView = (TextView) findViewById(R.id.coordinateTextView);

        tv_latitude = (TextView) findViewById(R.id.tv_coordinates_latitude_values);
        tv_longitude = (TextView) findViewById(R.id.tv_coordinates_longitude_values);
        tv_altitude = (TextView) findViewById(R.id.tv_coordinates_altitude_values);

        toggleButton = (ToggleButton) findViewById(R.id.tb_service);

        if (!runtimePermission())
            enableToggleButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (broadcastReceiver == null)
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        setCoordinatesText(intent.getExtras().get("Coordinates").toString());
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                restSender.sendDataToServer(); // TODO tutaj będzie strzelało do API na podstawie loginu i hasla
                            }
                        });
                    }
                };
            registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        } catch (Exception ex) {

        }
    }

    private void setCoordinatesText(String coordinates) {
        String[] coords = coordinates.split(" ");
        tv_longitude.setText(coords[0]);
        tv_latitude.setText(coords[1]);
        tv_altitude.setText(coords[2]);
    }

    private void enableToggleButton() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent service = new Intent(getApplicationContext(), GPSService.class);
                    Log.d("service_enabled", "GPS LOCALIZATION HAS BEEN ENABLED");
                    startService(service);
                    Toast.makeText(MainActivity.this, getString(R.string.app_service_enable_description), Toast.LENGTH_SHORT).show();
                } else {
                    Intent service = new Intent(getApplicationContext(), GPSService.class);
                    Log.d("service_disabled", "GPS LOCALIZATION HAS BEEN DISABLED");
                    stopService(service);
                    Toast.makeText(MainActivity.this, getString(R.string.app_service_disable_description), Toast.LENGTH_SHORT).show();
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
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    enableToggleButton();
                else
                    runtimePermission();
                return;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }


}
