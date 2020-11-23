package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.models.GetAllEventsModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.GetEventsRepository;

public class GetEventsViewModel extends ViewModel {
    private final GetEventsRepository getEventsRepository;

    public GetEventsViewModel() {
        super();
        getEventsRepository = new GetEventsRepository();
    }

    public LiveData<ObjectModel> getEventsLiveData(GetAllEventsModel model) {
        return getEventsRepository.getEvents(model);
    }
}
