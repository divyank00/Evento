package com.example.collegeproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.LikeEventModel;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.StandardResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeEventRepository {
    final MutableLiveData<ObjectModel> likeEventResponseLiveData;
    final MutableLiveData<ObjectModel> dislikeEventResponseLiveData;
    final ApiEndpointInterface authService;

    public LikeEventRepository() {
        likeEventResponseLiveData = new MutableLiveData<>();
        dislikeEventResponseLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> likeEvent(String eventId) {
        authService.likeEvent("Bearer " + StaticVariables.token, new LikeEventModel(eventId)).enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                if (response.isSuccessful()) {
                    likeEventResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            likeEventResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            likeEventResponseLiveData.postValue(new ObjectModel(false, response.body(), response.body().getMessage()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        likeEventResponseLiveData.postValue(new ObjectModel(false, response.body(), response.body().getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                likeEventResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return likeEventResponseLiveData;
    }

    public MutableLiveData<ObjectModel> dislikeEvent(String eventId) {
        authService.dislikeEvent("Bearer " + StaticVariables.token, new LikeEventModel(eventId)).enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getError())
                        dislikeEventResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                    else
                        dislikeEventResponseLiveData.postValue(new ObjectModel(false, response.body(), response.body().getMessage()));
                } else
                    dislikeEventResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                dislikeEventResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return dislikeEventResponseLiveData;
    }

}