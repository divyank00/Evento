package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.LikeEventRepository;

public class LikeEventViewModel extends ViewModel {
    private final LikeEventRepository likeEventRepository;

    public LikeEventViewModel() {
        super();
        likeEventRepository = new LikeEventRepository();
    }

    public LiveData<ObjectModel> likeEvent(String eventId) {
        return likeEventRepository.likeEvent(eventId);
    }

    public LiveData<ObjectModel> dislikeEvent(String eventId) {
        return likeEventRepository.dislikeEvent(eventId);
    }
}
