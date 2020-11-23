package com.example.collegeproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CategoryModel implements Serializable, Parcelable {

    private String _id;

    private String name;

    protected CategoryModel(Parcel in) {
        _id = in.readString();
        name = in.readString();
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    public void setId(String _id) {
        this._id = _id;
    }

    public String getId() {
        return this._id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
    }
}

