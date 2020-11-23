package com.example.collegeproject.response;

public class PostEventResponse {
    private boolean error;
    private String eventId;

    public boolean isError() {
        return error;
    }

    public String getEventId() {
        return eventId;
    }
}
