package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.models.PostReviewModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.PostReviewRepository;

public class PostReviewViewModel extends ViewModel {
    private final PostReviewRepository postReviewRepository;

    public PostReviewViewModel() {
        super();
        postReviewRepository = new PostReviewRepository();
    }

    public LiveData<ObjectModel> postReviewLiveData(PostReviewModel model) {
        return postReviewRepository.postReview(model);
    }
}
