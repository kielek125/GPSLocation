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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.malavero.trackyourchild.gpslocation.R;
import com.malavero.trackyourchild.gpslocation.helpers.SessionManager;
import com.malavero.trackyourchild.gpslocation.services.GPSService;
import com.malavero.trackyourchild.gpslocation.utils.RestSender;

public class MainActivity extends AppCompatActivity {

    private TextView textView, tv_latitude, tv_longitude, tv_altitude, tv_status;
    private ToggleButton toggleButton;
    private BroadcastReceiver broadcastReceiver;
    private RestSender restSender;
    private String login, password;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        restSender = new RestSender(login,password);
        textView = (TextView) findViewById(R.id.coordinateTextView);

        tv_latitude = (TextView) findViewById(R.id.tv_coordinates_latitude_values);
        tv_longitude = (TextView) findViewById(R.id.tv_coordinates_longitude_values);
        tv_altitude = (TextView) findViewById(R.id.tv_coordinates_altitude_values);
        tv_status = (TextView) findViewById(R.id.tv_status_info);

        toggleButton = (ToggleButton) findViewById(R.id.tb_service);


        if (!runtimePermission())
            enableToggleButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_logoff:
                // User chose the "logoff" action, mark the current item
                // as a favorite...
                session = new SessionManager(getApplicationContext());
                if (session.isLoggedIn()) {
                    session.setLogin(false);
                    // User wants to logoff. Take him to login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void enableToggleButton() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent service = new Intent(getApplicationContext(), GPSService.class);
                    Log.d("service_enabled", "GPS LOCALIZATION HAS BEEN ENABLED");
                    startService(service);
                    tv_status.setText(getString(R.string.app_service_enable_description));
                    //Toast.makeText(MainActivity.this, getString(R.string.app_service_enable_description), Toast.LENGTH_SHORT).show();
                } else {
                    Intent service = new Intent(getApplicationContext(), GPSService.class);
                    Log.d("service_disabled", "GPS LOCALIZATION HAS BEEN DISABLED");
                    stopService(service);
                    tv_status.setText(getString(R.string.app_service_disable_description));
                    //Toast.makeText(MainActivity.this, getString(R.string.app_service_disable_description), Toast.LENGTH_SHORT).show();
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
