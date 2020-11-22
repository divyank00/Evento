package com.example.collegeproject.network;

import com.example.collegeproject.models.LoginModel;
import com.example.collegeproject.models.RegisterModel;
import com.example.collegeproject.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiEndpointInterface {

    @POST("user/login")
    Call<UserResponse> login(@Body LoginModel model);

    @POST("user/register")
    Call<UserResponse> register(@Body RegisterModel modelString);
}