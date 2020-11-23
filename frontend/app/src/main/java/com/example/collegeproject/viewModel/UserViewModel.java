package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.models.LoginModel;
import com.example.collegeproject.models.RegisterModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    public UserViewModel() {
        super();
        userRepository = new UserRepository();
    }

    public LiveData<ObjectModel> loginLiveData(LoginModel model) {
        return userRepository.login(model);
    }

    public LiveData<ObjectModel> registerLiveData(RegisterModel model) {
        return userRepository.register(model);
    }

    public LiveData<ObjectModel> getUserInfo() {
        return userRepository.getUserInfo();
    }

    public LiveData<ObjectModel> logoutLiveData() {
        return userRepository.logout();
    }

    public LiveData<ObjectModel> postUserImageLiveData(byte[] imageBytes){
        return userRepository.postUserImage(imageBytes);
    }
    public LiveData<ObjectModel> removeUserImageLiveData(){
        return userRepository.removeUserImage();
    }
}
