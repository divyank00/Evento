package com.example.collegeproject.response;

import com.example.collegeproject.models.GetEventsModel;

import java.util.List;

public class GetEventsResponse {
    public List<GetEventsModel> getEvents() {
        return events;
    }

    private List<GetEventsModel> events;
    private boolean error;

    public boolean getError() {
        return this.error;
    }
}


