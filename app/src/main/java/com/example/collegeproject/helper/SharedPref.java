package com.example.collegeproject.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    static String token;
    static String name;

    static public void setToken(Context mContext, String auth) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", auth);
        editor.apply();
        token = auth;
    }

    static public String getToken(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        String auth = prefs.getString("token", null);
        token=auth;
        return auth;
    }
}
