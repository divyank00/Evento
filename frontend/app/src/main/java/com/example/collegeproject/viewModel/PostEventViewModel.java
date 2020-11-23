package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.models.PostEventModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.PostEventRepository;

public class PostEventViewModel extends ViewModel {
    private final PostEventRepository postEventRepository;

    public PostEventViewModel(){
        super();
        postEventRepository = new PostEventRepository();
    }

    public LiveData<ObjectModel> postEventLiveData(PostEventModel postEventModel){
        return postEventRepository.postEvent(postEventModel);
    }

    public LiveData<ObjectModel> postEventImageLiveData(String eventId, byte[] imageBytes){
        return postEventRepository.postEventImage(eventId, imageBytes);
    }
}
