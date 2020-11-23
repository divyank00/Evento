package com.example.collegeproject.response;

import com.example.collegeproject.models.CategoryModel;
import com.example.collegeproject.models.TypeModel;

import java.util.ArrayList;

public class GetTypesAndCategoriesResponse {
    private boolean error;
    private ArrayList<CategoryModel> Categories;

    public ArrayList<TypeModel> getTypes() {
        return Types;
    }

    private ArrayList<TypeModel> Types;

    public boolean getError() {
        return this.error;
    }

    public ArrayList<CategoryModel> getCategories() {
        return this.Categories;
    }
}


