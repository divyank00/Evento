package com.example.collegeproject.models;

public class LoginModel {

    private final String email;

    private final String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LoginModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
