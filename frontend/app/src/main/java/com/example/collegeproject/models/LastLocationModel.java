package com.example.collegeproject.models;

import android.os.Parcel;

import java.io.Serializable;

public class LastLocationModel implements Serializable {

    private String latitude, longitude;

    public LastLocationModel() {
        latitude="";
        longitude="";
    }

    public LastLocationModel(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected LastLocationModel(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

