package com.example.collegeproject.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostEventModel implements Serializable, Cloneable {

    private String name;
    private List<String> categories;
    private String description;
    private double price;
    private long startTime, endTime;
    private int noOfSeats;
    private OrganisationModel organizer;
    private String eventType;
    private CoordinateModel location;

    public PostEventModel() {
        name = "";
        categories = new ArrayList<>();
        description = "";
        eventType = "";
        price = -1;
        noOfSeats = -1;
        organizer = new OrganisationModel();
        location = new CoordinateModel();
    }

    @NonNull
    public PostEventModel clone() throws CloneNotSupportedException {
        return (PostEventModel) super.clone();
    }

    public OrganisationModel getOrganizer() {
        return organizer;
    }

    public void setOrganizer(OrganisationModel organizer) {
        this.organizer = organizer;
    }

    public void setAdd1(String title, String description, String organisationName, String organisationMail, String organisationContact) {
        this.name = title;
        this.description = description;
        this.organizer = new OrganisationModel(organisationName, organisationMail, organisationContact);
    }

    public void setAdd2(long startTime, long endTime, CoordinateModel coordinates) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = coordinates;
    }

    public void setAdd3(String type, List<String> categories, int noOfSeats, double price) {
        this.eventType = type;
        this.categories = categories;
        this.noOfSeats = noOfSeats;
        this.price = price;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public boolean newChanges(PostEventModel initialPostEventModel) {
        return !name.equals(initialPostEventModel.name) ||
                !description.equals(initialPostEventModel.description) ||
                price != initialPostEventModel.price ||
                startTime != initialPostEventModel.startTime ||
                endTime != initialPostEventModel.endTime ||
                noOfSeats != initialPostEventModel.noOfSeats ||
                !organizer.orgName.equals(initialPostEventModel.organizer.orgName) ||
                !organizer.orgEmail.equals(initialPostEventModel.organizer.orgEmail) ||
                !organizer.orgContactNo.equals(initialPostEventModel.organizer.orgContactNo) ||
                !eventType.equals(initialPostEventModel.eventType) ||
                !categories.equals(initialPostEventModel.categories) ||
                !location.getLongitude().equals(initialPostEventModel.getLocation().getLongitude()) ||
                !location.getLatitude().equals(initialPostEventModel.getLocation().getLatitude()) ||
                !location.getCity().equals(initialPostEventModel.getLocation().getCity());
    }

    public boolean isEmpty() {
        return name.isEmpty() &&
                description.isEmpty() &&
                price == -1 &&
                startTime == 0 &&
                endTime == 0 &&
                noOfSeats == -1 &&
                organizer.orgName.isEmpty() &&
                organizer.orgEmail.isEmpty() &&
                organizer.orgContactNo.isEmpty() &&
                eventType.isEmpty() &&
                categories.isEmpty() &&
                location.getLongitude().isEmpty() &&
                location.getLatitude().isEmpty() &&
                location.getCity().isEmpty();
    }
}

