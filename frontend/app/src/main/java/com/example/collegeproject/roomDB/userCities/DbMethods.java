package com.example.collegeproject.roomDB.userCities;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

public class DbMethods {
    public static class InsertDB implements Callable<Integer> {

        final WeakReference<Context> context;
        final String city;

        public InsertDB(WeakReference<Context> context, String city) {
            this.context = context;
            this.city = city;
        }

        @Override
        public Integer call() {
            if (context != null) {
                Database database = Database.getInstance(context.get());
                CitySearchModel CitySearchModel = new CitySearchModel(city,Calendar.getInstance().getTimeInMillis());
                database.Dao().insert(CitySearchModel);
                return 1;
            }
            return 0;
        }
    }

    public static class UpdateDB implements Callable<Integer> {

        final WeakReference<Context> context;
        final String city;

        public UpdateDB(WeakReference<Context> context, String city) {
            this.context = context;
            this.city = city;
        }

        @Override
        public Integer call() {
            if (context != null) {
                Database database = Database.getInstance(context.get());
                CitySearchModel CitySearchModel = new CitySearchModel(city,Calendar.getInstance().getTimeInMillis());
                database.Dao().update(CitySearchModel);
                return 1;
            }
            return 0;
        }
    }

    public static class DeleteOneDB implements Callable<Integer> {

        final WeakReference<Context> context;
        final String city;

        public DeleteOneDB(WeakReference<Context> context, String city) {
            this.context = context;
            this.city = city;
        }

        @Override
        public Integer call() {
            if (context != null) {
                Database database = Database.getInstance(context.get());
                database.Dao().deleteOne(city);
                return 1;
            }
            return 0;
        }
    }

    public static class DeleteAllDB implements Callable<Integer> {

        final WeakReference<Context> context;

        public DeleteAllDB(WeakReference<Context> context) {
            this.context = context;
        }

        @Override
        public Integer call() {
            if (context != null) {
                Database database = Database.getInstance(context.get());
                database.Dao().deleteAll();
                return 1;
            }
            return 0;
        }
    }

    public static class QueryDB implements Callable<List<CitySearchModel>> {

        final WeakReference<Context> context;

        public QueryDB(WeakReference<Context> context) {
            this.context = context;
        }

        @Override
        public List<CitySearchModel> call() {
            if (context != null) {
                Database database = Database.getInstance(context.get());
                return database.Dao().getRecentList();
            } else {
                return new ArrayList<>();
            }
        }
    }
}
