package com.example.collegeproject.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.collegeproject.repository.RoomRepository;

import java.util.List;

public class RoomViewModel extends AndroidViewModel {
    private final RoomRepository roomRepository;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        roomRepository = new RoomRepository();
    }

    public LiveData<List<String>> getUserCities() {
        return roomRepository.getUserCities(getApplication().getApplicationContext());
    }

    public void addUserCities(String city) {
        roomRepository.addUserCities(getApplication().getApplicationContext(), city);
    }

    public void clearUserCities() {
        roomRepository.deleteUserCities(getApplication().getApplicationContext());
    }
}