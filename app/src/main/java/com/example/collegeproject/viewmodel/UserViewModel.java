package com.example.collegeproject.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;

    public UserViewModel(){
        super();
        userRepository = new UserRepository();
    }

    public LiveData<ObjectModel> loginLiveData(String email, String password){
        return userRepository.login(email, password);
    }

    public LiveData<ObjectModel> registerLiveData(String name, String email, String password, String contactNumber){
        return userRepository.register(name, email, password, contactNumber);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
