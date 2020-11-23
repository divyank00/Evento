package com.example.collegeproject.models;

public class BookEventModel {
    public BookEventModel(String eventId, int noOfPeople) {
        this.eventId = eventId;
        this.noOfPeople = noOfPeople;
    }

    String eventId;
    int noOfPeople;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getNoOfPeople() {
        return noOfPeople;
    }

    public void setNoOfPeople(int noOfPeople) {
        this.noOfPeople = noOfPeople;
    }
}

