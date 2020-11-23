package com.example.collegeproject.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GetAllEventsModel implements Cloneable {

    private String city;
    private long date;
    private List<String> categories;

    public GetAllEventsModel() {
        city = "";
        date = 0;
        categories = new ArrayList<>();
    }

    public void clear() {
        city = "";
        date = 0;
        categories.clear();
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getCity() {
        return city;
    }

    public long getDate() {
        return date;
    }

    public List<String> getCategories() {
        return categories;
    }

    public boolean isDifferent(GetAllEventsModel model) {
        return !city.equals(model.getCity()) || date != model.getDate() || categories != model.getCategories();
    }

    @NonNull
    public GetAllEventsModel clone() throws CloneNotSupportedException {
        return (GetAllEventsModel) super.clone();
    }
}

