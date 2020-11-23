package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.GetFavouriteEventsRepository;

public class GetFavouriteEventsViewModel extends ViewModel {
    private final GetFavouriteEventsRepository getFavouriteEventsRepository;

    public GetFavouriteEventsViewModel() {
        super();
        getFavouriteEventsRepository = new GetFavouriteEventsRepository();
    }

    public LiveData<ObjectModel> getFavouriteEventsLiveData() {
        return getFavouriteEventsRepository.getFavouriteEvents();
    }
}
