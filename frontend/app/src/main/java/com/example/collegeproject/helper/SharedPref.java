package com.example.collegeproject.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    static public void setMail(Context mContext, String mail) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mail", mail);
        editor.apply();
    }

    static public String getMail(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getString("mail", "");
    }

    static public void setToken(Context mContext, String auth) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", auth);
        editor.apply();
        StaticVariables.token = auth;
    }

    static public String getToken(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        String auth = prefs.getString("token", null);
        StaticVariables.token = auth;
        return auth;
    }

    static public void clearData(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
    }
}
