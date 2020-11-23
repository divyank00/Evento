package com.example.collegeproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.GetFavouriteEventsResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetFavouriteEventsRepository {
    final MutableLiveData<ObjectModel> getFavouriteEventsLiveData;
    final ApiEndpointInterface authService;

    public GetFavouriteEventsRepository() {
        getFavouriteEventsLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> getFavouriteEvents() {
        authService.getFavouriteEvents("Bearer " + StaticVariables.token).enqueue(new Callback<GetFavouriteEventsResponse>() {
            @Override
            public void onResponse(Call<GetFavouriteEventsResponse> call, Response<GetFavouriteEventsResponse> response) {
                if (response.isSuccessful()) {
                    getFavouriteEventsLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            getFavouriteEventsLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            getFavouriteEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        getFavouriteEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<GetFavouriteEventsResponse> call, Throwable t) {
                getFavouriteEventsLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return getFavouriteEventsLiveData;
    }
}
