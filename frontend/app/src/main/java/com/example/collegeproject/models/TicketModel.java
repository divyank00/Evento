package com.example.collegeproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TicketModel implements Serializable, Parcelable {
    String ticketId;

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public static Creator<TicketModel> getCREATOR() {
        return CREATOR;
    }

    protected TicketModel(Parcel in) {
        ticketId = in.readString();
    }

    public static final Creator<TicketModel> CREATOR = new Creator<TicketModel>() {
        @Override
        public TicketModel createFromParcel(Parcel in) {
            return new TicketModel(in);
        }

        @Override
        public TicketModel[] newArray(int size) {
            return new TicketModel[size];
        }
    };

    public String getTicketId() {
        return ticketId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ticketId);
    }
}

