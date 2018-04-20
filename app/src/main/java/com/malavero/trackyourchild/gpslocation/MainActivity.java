package com.malavero.trackyourchild.gpslocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button buttonStart, buttonStop;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    private HttpURLConnection registerConnection;
    private HttpURLConnection loginConnection;
    private HttpURLConnection updateConnection;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (broadcastReceiver == null)
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        textView.setText("Coordinates: \n" + intent.getExtras().get("Coordinates"));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById(R.id.startTracingButton);
        buttonStop = (Button) findViewById(R.id.cancelTrackingButton);
        textView = (TextView) findViewById(R.id.coordinateTextView);

        if(!runtimePermission())
            enableButtons();
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
            String _response = convertStreamToString(is);
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
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void enableButtons() {
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent service = new Intent(getApplicationContext(), GPSService.class);
                startService(service);
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent service = new Intent(getApplicationContext(), GPSService.class);
                stopService(service);
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
                    enableButtons();
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
