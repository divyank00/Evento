package com.example.collegeproject.models;

import java.io.Serializable;

public class ReviewModel implements Serializable {

    private String user, event;
    private int rating;
    private String comment;
    private String createdAt;

    public String getUser() {
        return user;
    }

    public String getEvent() {
        return event;
    }

    public ReviewModel(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public ReviewModel() {
        rating = 0;
        comment = "";
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

