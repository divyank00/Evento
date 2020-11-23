package com.example.collegeproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.PostEventModel;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.PostEventResponse;
import com.example.collegeproject.response.StandardResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostEventRepository {
    final MutableLiveData<ObjectModel> eventResponseLiveData, eventImageResponseLiveData;
    final ApiEndpointInterface authService;

    public PostEventRepository() {
        eventResponseLiveData = new MutableLiveData<>();
        eventImageResponseLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> postEvent(PostEventModel model) {
        authService.postEvent("Bearer " + StaticVariables.token, model).enqueue(new Callback<PostEventResponse>() {
            @Override
            public void onResponse(Call<PostEventResponse> call, Response<PostEventResponse> response) {
                if (response.isSuccessful()) {
                    eventResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            eventResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            eventResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        eventResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<PostEventResponse> call, Throwable t) {
                eventResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return eventResponseLiveData;
    }

    public MutableLiveData<ObjectModel> postEventImage(String eventId, byte[] imageBytes) {
        RequestBody requestFile = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        authService.postEventImage("Bearer " + StaticVariables.token, eventId, body).enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                if (response.isSuccessful()) {
                    eventImageResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            eventImageResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            eventImageResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        eventImageResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                eventImageResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return eventImageResponseLiveData;
    }

}