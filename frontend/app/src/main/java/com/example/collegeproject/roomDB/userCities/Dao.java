package com.example.collegeproject.roomDB.userCities;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface Dao {

    @Query("SELECT * FROM RecentSearches ORDER BY timestamp DESC LIMIT 10")
    List<CitySearchModel> getRecentList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CitySearchModel CitySearchModel);

    @Update
    void update(CitySearchModel CitySearchModel);

    @Query("DELETE FROM RecentSearches WHERE city= :city")
    void deleteOne(String city);

    @Query("DELETE FROM RecentSearches")
    void deleteAll();
}
