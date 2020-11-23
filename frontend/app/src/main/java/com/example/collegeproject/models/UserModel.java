package com.example.collegeproject.models;

public class UserModel {
    private boolean error;
    private String name, email,contactNumber, _id;
    private LastLocationModel lastLocation;

    public UserModel() {
        name="";
        email="";
        contactNumber="";
        _id="";
        lastLocation = new LastLocationModel();
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String get_id() {
        return _id;
    }

    public LastLocationModel getLastLocation() {
        return lastLocation;
    }

    public boolean getError() {
        return error;
    }

    public String getName() {
        return name;
    }

}
