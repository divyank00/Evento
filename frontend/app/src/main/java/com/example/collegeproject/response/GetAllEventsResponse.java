package com.example.collegeproject.response;

import com.example.collegeproject.models.EventDetailsModel;

import java.util.List;

public class GetAllEventsResponse {
    public List<EventDetailsModel> getEvents() {
        return sorted;
    }

    private List<EventDetailsModel> sorted;
    private boolean error;

    public boolean getError() {
        return this.error;
    }
}


