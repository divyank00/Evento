package com.example.collegeproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.GetAllRegisteredEventsResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetRegisteredEventsRepository {
    final MutableLiveData<ObjectModel> getRegisteredLiveEventsLiveData;
    final MutableLiveData<ObjectModel> getRegisteredPastEventsLiveData;
    final ApiEndpointInterface authService;

    public GetRegisteredEventsRepository() {
        getRegisteredLiveEventsLiveData = new MutableLiveData<>();
        getRegisteredPastEventsLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> getRegisteredLiveEvents() {
        authService.getRegisteredLiveEvents("Bearer " + StaticVariables.token).enqueue(new Callback<GetAllRegisteredEventsResponse>() {
            @Override
            public void onResponse(Call<GetAllRegisteredEventsResponse> call, Response<GetAllRegisteredEventsResponse> response) {
                if (response.isSuccessful()) {
                    getRegisteredLiveEventsLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            getRegisteredLiveEventsLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            getRegisteredLiveEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        getRegisteredLiveEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAllRegisteredEventsResponse> call, Throwable t) {
                getRegisteredLiveEventsLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return getRegisteredLiveEventsLiveData;
    }

    public MutableLiveData<ObjectModel> getRegisteredPastEvents() {
        authService.getRegisteredPastEvents("Bearer " + StaticVariables.token).enqueue(new Callback<GetAllRegisteredEventsResponse>() {
            @Override
            public void onResponse(Call<GetAllRegisteredEventsResponse> call, Response<GetAllRegisteredEventsResponse> response) {
                if (response.isSuccessful()) {
                    getRegisteredPastEventsLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            getRegisteredPastEventsLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            getRegisteredPastEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        getRegisteredPastEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAllRegisteredEventsResponse> call, Throwable t) {
                getRegisteredPastEventsLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return getRegisteredPastEventsLiveData;
    }

}
