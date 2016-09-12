package com.aliasapps.sports.main.profile;

import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.Sport;
import com.aliasapps.sports.entity.Trophy;
import com.aliasapps.sports.entity.TrophyUserRelation;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by evanchen on 16-06-06.
 */
public class ProfileServerCalls {

    public static void getUserProfileData(final VolleyCallback callback, final User user, String endpoint){
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", user.getToken());
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    //return all upcoming games
    public static void parseUserProfileDataUpcoming(JSONObject response, List<Game> upcomingGames){
        Game game;
        Sport sport;

        try {
            JSONArray upcomingGamesJsonObject = response.getJSONArray("upcoming_games");

            for (int i = 0; i < upcomingGamesJsonObject.length(); i++) {

                JSONObject gameJsonObject = upcomingGamesJsonObject.getJSONObject(i);
                JSONObject sportJsonObject = gameJsonObject.getJSONObject("sport");

                sport = new Sport(sportJsonObject.getInt("id"), sportJsonObject.getString("name"), sportJsonObject.getString("icon"), sportJsonObject.getString("map_icon"));

                game = new Game(gameJsonObject.getInt("id"), gameJsonObject.getString("name"), gameJsonObject.getString("description")
                        , gameJsonObject.getString("address"), gameJsonObject.getString("location_name"), gameJsonObject.getDouble("latitude"), gameJsonObject.getDouble("longitude")
                        , gameJsonObject.getInt("min_players"), gameJsonObject.getInt("max_players"), gameJsonObject.getDouble("cost"), gameJsonObject.getInt("intensity")
                        , gameJsonObject.getInt("gender"), gameJsonObject.getInt("user_id"), gameJsonObject.getString("game_time"), sport);

                upcomingGames.add(game);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //get the upcoming games data immediately
     //  ProfileUpcomingFragment.profileUpcomingGameAdapter.notifyDataSetChanged();

    }

    //return all past games
    public static void parseUserProfileDataPastGame(JSONObject response, List<Game> pastGames){
        Game game;
        Sport sport;

        try {
            JSONArray pastGameJsonObject = response.getJSONArray("past_games");
            for (int i = 0; i < pastGameJsonObject.length(); i++) {
                JSONObject gameJsonObject = pastGameJsonObject.getJSONObject(i);
                JSONObject sportJsonObject = gameJsonObject.getJSONObject("sport");

                sport = new Sport(sportJsonObject.getInt("id"), sportJsonObject.getString("name"), sportJsonObject.getString("icon"), sportJsonObject.getString("map_icon"));

                game = new Game(gameJsonObject.getInt("id"), gameJsonObject.getString("name"), gameJsonObject.getString("description")
                        , gameJsonObject.getString("address"), gameJsonObject.getString("location_name"), gameJsonObject.getDouble("latitude"), gameJsonObject.getDouble("longitude")
                        , gameJsonObject.getInt("min_players"), gameJsonObject.getInt("max_players"), gameJsonObject.getDouble("cost"), gameJsonObject.getInt("intensity")
                        , gameJsonObject.getInt("gender"), gameJsonObject.getInt("user_id"), gameJsonObject.getString("game_time"), sport);

                pastGames.add(game);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        //get the past games data immediately
//        ProfilePastFragment.profilePastGameAdapter.notifyDataSetChanged();
    }

    //return all trophies
    public static void parseUserProfileDataTrophies(JSONObject response, List<TrophyUserRelation> trophies){
        Trophy trophy;
        TrophyUserRelation trophyUserRelation;

        try {
            JSONArray trophiesJsonObject = response.getJSONArray("trophies");
            for (int i = 0; i < trophiesJsonObject.length(); i++) {
                JSONObject trophyUserRelationJsonObject = trophiesJsonObject.getJSONObject(i);
                JSONObject trophyJsonObject = trophyUserRelationJsonObject.getJSONObject("trophy");

                trophy = new Trophy(trophyJsonObject.getInt("id"), trophyJsonObject.getString("description"), trophyJsonObject.getString("name"), trophyJsonObject.getString("icon"));

                trophyUserRelation = new TrophyUserRelation(trophyUserRelationJsonObject.getInt("id"), trophyUserRelationJsonObject.getInt("user_id"), trophy);

                trophies.add(trophyUserRelation);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
//       ProfileTrophiesFragment.profileTrophiesAdapter.notifyDataSetChanged();
    }



}
