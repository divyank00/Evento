package com.example.collegeproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TypeModel implements Serializable, Parcelable {

    private String _id;
    private String name;

    protected TypeModel(Parcel in) {
        _id = in.readString();
        name = in.readString();
    }

    public static final Creator<TypeModel> CREATOR = new Creator<TypeModel>() {
        @Override
        public TypeModel createFromParcel(Parcel in) {
            return new TypeModel(in);
        }

        @Override
        public TypeModel[] newArray(int size) {
            return new TypeModel[size];
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

