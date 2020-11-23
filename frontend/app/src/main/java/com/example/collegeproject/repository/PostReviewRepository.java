package com.example.collegeproject.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.PostReviewModel;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.StandardResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostReviewRepository {
    final MutableLiveData<ObjectModel> postReviewResponseLiveData;
    final ApiEndpointInterface authService;

    public PostReviewRepository() {
        postReviewResponseLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> postReview(PostReviewModel model) {
        authService.reviewEvent("Bearer " + StaticVariables.token, model).enqueue(new Callback<StandardResponse>() {
            @Override
            public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                Log.d("PostFeed",response.code()+" "+response.body());
                if (response.isSuccessful()) {
                    Log.d("PostFeed","True");
                    postReviewResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        Log.d("PostFeed","False");
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            postReviewResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            postReviewResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        postReviewResponseLiveData.postValue(new ObjectModel(false, null, response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardResponse> call, Throwable t) {
                postReviewResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return postReviewResponseLiveData;
    }
}