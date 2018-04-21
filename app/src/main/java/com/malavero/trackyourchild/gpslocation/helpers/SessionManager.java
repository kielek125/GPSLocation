package com.malavero.trackyourchild.gpslocation.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String TOKEN = "Autentication";

    private static final String RECENTLYLOGGED = "Email";

    private static final String LASTKNOWNPASS = "Pass";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setToken(String token) {

        editor.putString(TOKEN, token);
        editor.commit();

        Log.d(TAG, "Token added successfully");
    }

    public String getToken() {
        return pref.getString(TOKEN, "");
    }

    public String getRecentlyLogged() {
        return pref.getString(RECENTLYLOGGED, "");
    }

    public void setRecentlyLogged(String email) {
        editor.putString(RECENTLYLOGGED, email);
        editor.commit();

        Log.d(TAG, "Email added successfully");
    }

    public String getPassword() {
        return pref.getString(LASTKNOWNPASS, "");
    }

    public void setPassword(String password) {
        editor.putString(LASTKNOWNPASS, password);
        editor.commit();

        Log.d(TAG, "Password added successfully");
    }


}