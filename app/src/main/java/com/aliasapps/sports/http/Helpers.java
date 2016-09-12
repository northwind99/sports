package com.aliasapps.sports.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aliasapps.sports.entity.User;
import com.google.gson.Gson;

/**
 * Created by evanchen on 16-03-29.
 */
public class Helpers {

    public static final String BASE_URL = "http://52.201.240.121:8080/sports/api";

    public static final String BASE_SECOND_URL = "http://52.201.240.121:8080/sports_dev/api";

    public Helpers(){}

    public static User getUser(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = preferences.getString("User", "");
        return gson.fromJson(json, User.class);
    }

    public static void saveUserToDisk(Context context, User user){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("User", json);
        prefsEditor.apply();
    }

    public static void clearSharedPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }


}
