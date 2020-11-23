package com.example.collegeproject.models;

public class RegisteredEventDetailsModel {
    GetEventsModel eventDetails;
    boolean liked;
    boolean started;

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    boolean end;

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean like) {
        this.liked = like;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public GetEventsModel getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(GetEventsModel eventDetails) {
        this.eventDetails = eventDetails;
    }
}

