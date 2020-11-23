package com.example.collegeproject.models;

import java.io.Serializable;

public class PostReviewModel implements Serializable {

    private int rating;
    private String comment,eventId;

    public PostReviewModel() {
        rating=0;
        comment="";
    }

    public PostReviewModel(int rating, String comment, String eventId) {
        this.rating = rating;
        this.comment = comment;
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

