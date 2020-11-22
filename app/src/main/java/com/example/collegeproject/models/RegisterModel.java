package com.example.collegeproject.models;

public class RegisterModel {
    private String name;
    private String email;
    private String password;
    private String contactNumber;

    public RegisterModel(String name, String email, String password, String contactNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
    }
}
