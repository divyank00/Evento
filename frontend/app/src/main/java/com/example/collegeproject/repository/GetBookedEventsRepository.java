package com.example.collegeproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.GetAllBookedEventsResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetBookedEventsRepository {
    final MutableLiveData<ObjectModel> getBookedLiveEventsLiveData;
    final MutableLiveData<ObjectModel> getBookedPastEventsLiveData;
    final ApiEndpointInterface authService;

    public GetBookedEventsRepository() {
        getBookedLiveEventsLiveData = new MutableLiveData<>();
        getBookedPastEventsLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> getBookedLiveEvents() {
        authService.getBookedLiveEvents("Bearer " + StaticVariables.token).enqueue(new Callback<GetAllBookedEventsResponse>() {
            @Override
            public void onResponse(Call<GetAllBookedEventsResponse> call, Response<GetAllBookedEventsResponse> response) {
                if (response.isSuccessful()) {
                    getBookedLiveEventsLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            getBookedLiveEventsLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            getBookedLiveEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        getBookedLiveEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAllBookedEventsResponse> call, Throwable t) {
                getBookedLiveEventsLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return getBookedLiveEventsLiveData;
    }

    public MutableLiveData<ObjectModel> getBookedPastEvents() {
        authService.getBookedPastEvents("Bearer " + StaticVariables.token).enqueue(new Callback<GetAllBookedEventsResponse>() {
            @Override
            public void onResponse(Call<GetAllBookedEventsResponse> call, Response<GetAllBookedEventsResponse> response) {
                if (response.isSuccessful()) {
                    getBookedPastEventsLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            getBookedPastEventsLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            getBookedPastEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        getBookedPastEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAllBookedEventsResponse> call, Throwable t) {
                getBookedPastEventsLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return getBookedPastEventsLiveData;
    }

}
