package com.malavero.trackyourchild.gpslocation.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Kiełson on 21.04.2018.
 */

public class RestSender {
    String login;
    String password;
    String mail;
    long latitude, longitude;
    RequestType type;

    public RestSender(String login, String password, String mail){
        this.login = login;
        this.password = password;
        this.mail = mail;
        type = RequestType.REGISTER;
    }
    public RestSender(String login, String password){
        this.login = login;
        this.password = password;
        type = RequestType.LOGIN;
    }
    public RestSender(long latitude, long longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        type = RequestType.COORDINATES;
    }

    private String RegisterUser() {
        final JSONObject root = new JSONObject();
        try {
            root.put("name", login);
            root.put("email", password);
            root.put("password", mail);
            return root.toString();
        } catch (Exception ex) {
            Log.d("JWP", "Can't format JSON");
        }
        return null;
    }
    private String LoginUser() {
        final JSONObject root = new JSONObject();
        try {
            root.put("email", login);
            root.put("password", password);
            return root.toString();
        } catch (Exception ex) {
            Log.d("JWP", "Can't format JSON");
        }
        return null;
    }
    private String CoordinateUser() {
        final JSONObject root = new JSONObject();
        try {
            root.put("longitude", longitude);
            root.put("latitude", latitude);
            root.put("device_name", Build.ID);
            return root.toString();
        } catch (Exception ex) {
            Log.d("JWP", "Can't format JSON");
        }
        return null;
    }

    public void sendDataToServer() {
        final String json;
        switch (type){
            case REGISTER:
                json = RegisterUser();
                break;
            case LOGIN:
                json = LoginUser();
                break;
            case COORDINATES:
                json = CoordinateUser();
                break;
            default:
                json = null;
        }

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return getServerResponse(json);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //TODO
            }
        }.execute();
    }

    private String getServerResponse(String json) {
        if(json == null)
            return null;
        HttpPost post;
        switch (type){
            case REGISTER:
                post = new HttpPost("http://geoloc.yorki-dev.com/api/auth/register");
                break;
            case LOGIN:
                post = new HttpPost("http://geoloc.yorki-dev.com/api/auth/login");
                break;
            case COORDINATES:
                post = new HttpPost("http://geoloc.yorki-dev.com/api/location/update");
                break;
            default:
                post = null;
        }
        if(post == null)
            return null;
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler responseHandler = new BasicResponseHandler();
            String serverResponse = client.execute(post, responseHandler);
            return serverResponse;

        } catch (UnsupportedEncodingException e) {
            Log.d("JWP", e.toString());
        } catch (ClientProtocolException e) {
            Log.d("JWP", e.toString());
        } catch (IOException e) {
            Log.d("JWP", e.toString());
        } catch (Exception e){
            Log.d("JWP", e.toString());
        }
        return null;
    }
    private enum RequestType{
        REGISTER,
        LOGIN,
        COORDINATES
    }
}
