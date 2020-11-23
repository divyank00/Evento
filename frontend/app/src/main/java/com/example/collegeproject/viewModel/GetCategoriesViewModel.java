package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.GetTypesCategoriesRepository;

public class GetCategoriesViewModel extends ViewModel {
    private final GetTypesCategoriesRepository getTypesCategoriesRepository;

    public GetCategoriesViewModel(){
        super();
        getTypesCategoriesRepository = new GetTypesCategoriesRepository();
    }

    public LiveData<ObjectModel> getTypesAndCategoriesLiveData(){
        return getTypesCategoriesRepository.getTypesAndCategories();
    }

    public LiveData<ObjectModel> rebootServerLiveData(){
        return getTypesCategoriesRepository.rebootServer();
    }
}
