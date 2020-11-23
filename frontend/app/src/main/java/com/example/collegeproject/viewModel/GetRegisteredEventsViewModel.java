package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.GetRegisteredEventsRepository;

public class GetRegisteredEventsViewModel extends ViewModel {
    private final GetRegisteredEventsRepository getRegisteredEventsRepository;

    public GetRegisteredEventsViewModel() {
        super();
        getRegisteredEventsRepository = new GetRegisteredEventsRepository();
    }

    public LiveData<ObjectModel> getRegisteredLiveData() {
        return getRegisteredEventsRepository.getRegisteredLiveEvents();
    }

    public LiveData<ObjectModel> getRegisteredPastData() {
        return getRegisteredEventsRepository.getRegisteredPastEvents();
    }
}
