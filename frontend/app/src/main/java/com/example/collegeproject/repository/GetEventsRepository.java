package com.example.collegeproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.GetAllEventsModel;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.GetAllEventsResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetEventsRepository {
    final MutableLiveData<ObjectModel> getEventsLiveData;
    final ApiEndpointInterface authService;

    public GetEventsRepository() {
        getEventsLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> getEvents(GetAllEventsModel model) {
        authService.getEvents("Bearer " + StaticVariables.token,model).enqueue(new Callback<GetAllEventsResponse>() {
            @Override
            public void onResponse(Call<GetAllEventsResponse> call, Response<GetAllEventsResponse> response) {
                if (response.isSuccessful()) {
                    getEventsLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            getEventsLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            getEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        getEventsLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAllEventsResponse> call, Throwable t) {
                getEventsLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return getEventsLiveData;
    }
}
