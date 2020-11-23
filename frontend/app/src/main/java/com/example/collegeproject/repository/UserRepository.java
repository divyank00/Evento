package com.example.collegeproject.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.LoginModel;
import com.example.collegeproject.models.RegisterModel;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.StandardResponse;
import com.example.collegeproject.response.UserResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    final MutableLiveData<ObjectModel> userResponseLiveData, profileResponseLiveData;
    final ApiEndpointInterface authService;

    public UserRepository() {
        userResponseLiveData = new MutableLiveData<>();
        profileResponseLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> login(LoginModel model) {
        authService.login(model).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    userResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                userResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return userResponseLiveData;
    }

    public MutableLiveData<ObjectModel> register(RegisterModel model) {
        authService.register(model).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    userResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                userResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return userResponseLiveData;
    }

    public MutableLiveData<ObjectModel> getUserInfo() {
        authService.getUserInfo("Bearer " + StaticVariables.token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    userResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                userResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return userResponseLiveData;
    }

    public MutableLiveData<ObjectModel> logout() {
        authService.logOut("Bearer " + StaticVariables.token).enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                if (response.isSuccessful()) {
                    userResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        userResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                userResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return userResponseLiveData;
    }

    public MutableLiveData<ObjectModel> postUserImage(byte[] imageBytes) {
        RequestBody requestFile = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("profilepic", "image.jpg", requestFile);
        authService.postUserImage("Bearer " + StaticVariables.token, body).enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                if (response.isSuccessful()) {
                    profileResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            profileResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            profileResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        profileResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                profileResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return profileResponseLiveData;
    }

    public MutableLiveData<ObjectModel> removeUserImage() {
        authService.removeUserImage("Bearer " + StaticVariables.token).enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                if (response.isSuccessful()) {
                    profileResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            profileResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            profileResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        profileResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                profileResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return profileResponseLiveData;
    }

}