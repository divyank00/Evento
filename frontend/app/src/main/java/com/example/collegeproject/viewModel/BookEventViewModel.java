package com.example.collegeproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.collegeproject.models.BookEventModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.repository.BookEventRepository;

public class BookEventViewModel extends ViewModel {
    private final BookEventRepository bookEventRepository;

    public BookEventViewModel() {
        super();
        bookEventRepository = new BookEventRepository();
    }

    public LiveData<ObjectModel> bookEvent(BookEventModel model) {
        return bookEventRepository.bookEvent(model);
    }
}
