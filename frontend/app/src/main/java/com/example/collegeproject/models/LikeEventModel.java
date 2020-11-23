package com.example.collegeproject.models;

public class LikeEventModel {
    public LikeEventModel(String eventId) {
        this.eventId = eventId;
    }

    String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

}

