package com.example.collegeproject.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.collegeproject.roomDB.TaskRunner;
import com.example.collegeproject.roomDB.userCities.CitySearchModel;
import com.example.collegeproject.roomDB.userCities.DbMethods;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {
    final MutableLiveData<List<String>> userCityLiveData;
    final TaskRunner taskRunner;

    public RoomRepository() {
        userCityLiveData = new MutableLiveData<>();
        taskRunner = new TaskRunner();
    }

    public MutableLiveData<List<String>> getUserCities(Context context) {
        taskRunner.executeAsync(new DbMethods.QueryDB(new WeakReference<>(context)), result -> {
            List<String> res = new ArrayList<>();
            for (CitySearchModel m : result) {
                res.add(m.getCity());
            }
            userCityLiveData.postValue(res);
        });
        return userCityLiveData;
    }

    public void addUserCities(Context context, String city) {
        taskRunner.executeAsync(new DbMethods.InsertDB(new WeakReference<>(context), city), result -> {
        });
    }

    public void deleteUserCities(Context context) {
        taskRunner.executeAsync(new DbMethods.DeleteAllDB(new WeakReference<>(context)), result -> {
        });
    }
}