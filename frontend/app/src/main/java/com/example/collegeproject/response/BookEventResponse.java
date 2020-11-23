package com.example.collegeproject.response;

import com.example.collegeproject.models.TicketModel;

import java.util.List;

public class BookEventResponse {
    private boolean error;
    private List<TicketModel> tickets;

    public List<TicketModel> getTickets() {
        return tickets;
    }

    public boolean isError() {
        return error;
    }
}


