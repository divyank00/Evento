package com.example.collegeproject.models;

import java.util.ArrayList;

public class BookedEventDetailsModel {
    private GetEventsModel eventDetails;
    private ArrayList<TicketModel> tickets;
    private int noOfPeople;
    private boolean liked;
    private boolean started;
    private boolean end;

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getNoOfPeople() {
        return noOfPeople;
    }

    public void setNoOfPeople(int noOfPeople) {
        this.noOfPeople = noOfPeople;
    }

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

    public ArrayList<TicketModel> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<TicketModel> tickets) {
        this.tickets = tickets;
    }

    public GetEventsModel getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(GetEventsModel eventDetails) {
        this.eventDetails = eventDetails;
    }
}

