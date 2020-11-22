package com.example.collegeproject.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.models.LoginModel;
import com.example.collegeproject.models.RegisterModel;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    MutableLiveData<ObjectModel> userResponseLiveData;
    ApiEndpointInterface authService;

    public UserRepository() {
        userResponseLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> login(String email, String password) {
        authService.login(new LoginModel(email, password)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.d("ErrorCode: ", ""+response.code());
                if (response.isSuccessful()) {
                    if (!response.body().getError())
                        userResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                    else
                        userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.body().getMessage()));
                } else
                    userResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                userResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return userResponseLiveData;
    }

    public MutableLiveData<ObjectModel> register(String name, String email, String password, String contactNumber) {
        authService.register(new RegisterModel(name, email, password, contactNumber)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.d("ErrorCode: ", ""+response.code());
                if (response.isSuccessful()) {
                    if (!response.body().getError())
                        userResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                    else
                        userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.body().getMessage()));
                } else
                    userResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                userResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return userResponseLiveData;
    }
}