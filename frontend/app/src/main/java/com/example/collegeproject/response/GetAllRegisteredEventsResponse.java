package com.example.collegeproject.response;

import com.example.collegeproject.models.RegisteredEventDetailsModel;

import java.util.List;

public class GetAllRegisteredEventsResponse {

    private List<RegisteredEventDetailsModel> registeredEvents;
    private boolean error;

    public List<RegisteredEventDetailsModel> getRegisteredEvents() {
        return registeredEvents;
    }
}


