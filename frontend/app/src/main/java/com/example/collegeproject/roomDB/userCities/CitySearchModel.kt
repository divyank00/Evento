package com.example.collegeproject.roomDB.userCities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RecentSearches")
data class CitySearchModel(
        @PrimaryKey
        val city: String,
        val timestamp: Long
)