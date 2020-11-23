package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.GetBookedEventsRepository;

public class GetBookedEventsViewModel extends ViewModel {
    private final GetBookedEventsRepository getBookedEventsRepository;

    public GetBookedEventsViewModel() {
        super();
        getBookedEventsRepository = new GetBookedEventsRepository();
    }

    public LiveData<ObjectModel> getBookedLiveData() {
        return getBookedEventsRepository.getBookedLiveEvents();
    }

    public LiveData<ObjectModel> getBookedPastData() {
        return getBookedEventsRepository.getBookedPastEvents();
    }
}
