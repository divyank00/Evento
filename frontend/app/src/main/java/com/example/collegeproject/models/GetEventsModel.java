package com.example.collegeproject.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetEventsModel implements Serializable {

    private String _id;
    private String name;
    private List<String> categories;
    private String description;
    private double price;
    private double avgRating;
    private double totalRating;
    private double totalStar;
    private String startTime, endTime;
    private int noOfSeats, availableSeats;
    private GetOrganisationModel organizer;
    private String eventType;
    private CoordinateModel location;
    private List<ReviewModel> reviews;
    private boolean liked;
    private boolean started;
    private boolean imageExists;

    public boolean isImageExists() {
        return imageExists;
    }

    public void setImageExists(boolean imageExists) {
        this.imageExists = imageExists;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getTotalStar() {
        return totalStar;
    }

    public void setTotalStar(double totalStar) {
        this.totalStar = totalStar;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    private boolean end;

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public GetEventsModel() {
        name = "";
        categories = new ArrayList<>();
        description = "";
        eventType = "";
        startTime = "";
        endTime = "";
        price = -1;
        liked = false;
        noOfSeats = -1;
        avgRating = 0;
        totalRating = 0;
        availableSeats = -1;
        totalStar=0;
        organizer = new GetOrganisationModel();
        location = new CoordinateModel();
        reviews = new ArrayList<>();
        imageExists = true;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public GetOrganisationModel getOrganizer() {
        return organizer;
    }

    public void setOrganizer(GetOrganisationModel organizer) {
        this.organizer = organizer;
    }

    public List<ReviewModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    public CoordinateModel getLocation() {
        return location;
    }

    public void setLocation(CoordinateModel location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}

