package com.example.collegeproject.response;

import com.example.collegeproject.models.BookedEventDetailsModel;

import java.util.List;

public class GetAllBookedEventsResponse {

    private List<BookedEventDetailsModel> bookedEvents;
    private boolean error;

    public List<BookedEventDetailsModel> getBookedEvents() {
        return bookedEvents;
    }
}


