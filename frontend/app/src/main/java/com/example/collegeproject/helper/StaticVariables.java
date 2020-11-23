package com.example.collegeproject.helper;

import com.example.collegeproject.models.CategoryModel;
import com.example.collegeproject.models.TypeModel;
import com.example.collegeproject.models.UserModel;

import java.util.ArrayList;

public class StaticVariables {
    public static final String ImagePath = "http://dbms-event-booking.herokuapp.com/api/event/image/";
    public static final String ProfilePath = "http://dbms-event-booking.herokuapp.com/api/user/profilepic/";
    public static String token;
    public static UserModel user;
    public static ArrayList<CategoryModel> eventCategories;
    public static ArrayList<TypeModel> eventTypes;
}
