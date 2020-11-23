package com.example.collegeproject.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.BookEventModel;
import com.example.collegeproject.network.ApiEndpointInterface;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.network.RemoteDataSource;
import com.example.collegeproject.response.BookEventResponse;

import org.json.JSONObject;

import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookEventRepository {
    final MutableLiveData<ObjectModel> bookEventResponseLiveData;
    final ApiEndpointInterface authService;

    public BookEventRepository() {
        bookEventResponseLiveData = new MutableLiveData<>();
        authService = RemoteDataSource.createService(ApiEndpointInterface.class);
    }

    public MutableLiveData<ObjectModel> bookEvent(BookEventModel model) {
        authService.bookEvent("Bearer " + StaticVariables.token, model).enqueue(new Callback<BookEventResponse>() {
            @Override
            public void onResponse(Call<BookEventResponse> call, Response<BookEventResponse> response) {
                if (response.isSuccessful()) {
                    bookEventResponseLiveData.postValue(new ObjectModel(true, response.body(), response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            bookEventResponseLiveData.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else
                            bookEventResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        bookEventResponseLiveData.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<BookEventResponse> call, Throwable t) {
                bookEventResponseLiveData.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return bookEventResponseLiveData;
    }
}