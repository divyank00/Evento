package com.example.collegeproject.models;

public class RegisterModel {
    private final String name;
    private final String email;
    private final String password;
    private final String contactNumber;

    public RegisterModel(String name, String email, String password, String contactNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
    }
}
