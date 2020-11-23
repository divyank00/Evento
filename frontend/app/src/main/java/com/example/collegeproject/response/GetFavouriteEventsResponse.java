package com.example.collegeproject.response;

import com.example.collegeproject.models.EventDetailsModel;

import java.util.List;

public class GetFavouriteEventsResponse {


    public List<EventDetailsModel> getLikedEvents() {
        return likedEvents;
    }

    private List<EventDetailsModel> likedEvents;
    private boolean error;

    public boolean getError() {
        return this.error;
    }
}


