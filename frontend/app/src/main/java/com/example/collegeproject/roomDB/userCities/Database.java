package com.example.collegeproject.roomDB.userCities;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = CitySearchModel.class, exportSchema = false, version = 1)
public abstract class Database extends RoomDatabase {
    private static final String db_name = "recent_search_db";
    private static Database instance;

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, db_name)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    public abstract Dao Dao();
}
