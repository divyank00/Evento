package com.example.collegeproject.response;

import com.example.collegeproject.models.UserModel;

public class UserResponse {
    UserModel user;
    String token;

    public String getToken() {
        return token;
    }

    public UserModel getUser() {
        return user;
    }
}
