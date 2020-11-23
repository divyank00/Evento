package com.example.collegeproject.models;

import android.os.Parcel;

import java.io.Serializable;

public class CoordinateModel implements Serializable {

    private String latitude, longitude, city;

    public CoordinateModel() {
        latitude="";
        longitude="";
        city="";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public CoordinateModel(String latitude, String longitude, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
    }

    protected CoordinateModel(Parcel in) {
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

