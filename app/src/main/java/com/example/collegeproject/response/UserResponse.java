package com.example.collegeproject.response;

public class UserResponse {
    private boolean error;
    private String token;
    private String name;
    private String message;

    public boolean getError() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
