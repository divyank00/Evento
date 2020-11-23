package com.example.collegeproject.models;

public class EventDetailsModel {
    private GetEventsModel eventDetails;
    private boolean liked;
    private boolean started;
    private boolean end;

    public boolean isEnd() {
        return end;
    }


    public boolean isStarted() {
        return started;
    }

    public boolean isLiked() {
        return liked;
    }

    public GetEventsModel getEventDetails() {
        return eventDetails;
    }
}

