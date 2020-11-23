package com.example.collegeproject.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.GetTypesAndCategoriesResponse;
import com.example.collegeproject.response.StandardResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetTypesCategoriesRepository {
    final MutableLiveData<ObjectModel> typesAndCategoriesLiveData, rebootServerLiveData;
    final ApiEndpointInterface authService;

    public GetTypesCategoriesRepository() {
        typesAndCategoriesLiveData = new MutableLiveData<>();
        rebootServerLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> getTypesAndCategories() {
        authService.getTypesAndCategories().enqueue(new Callback<GetTypesAndCategoriesResponse>() {
            @Override
            public void onResponse(Call<GetTypesAndCategoriesResponse> call, Response<GetTypesAndCategoriesResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("Response",response.body().getError()+"");
                    typesAndCategoriesLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            typesAndCategoriesLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            typesAndCategoriesLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        typesAndCategoriesLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<GetTypesAndCategoriesResponse> call, Throwable t) {
                typesAndCategoriesLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return typesAndCategoriesLiveData;
    }

    public MutableLiveData<ObjectModel> rebootServer() {
        authService.rebootServer().enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                if (response.isSuccessful()) {
                    rebootServerLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            rebootServerLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            rebootServerLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        rebootServerLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                rebootServerLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return rebootServerLiveData;
    }

}