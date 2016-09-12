package com.aliasapps.sports.entity;

import java.io.Serializable;

/**
 * Created by evanchen on 16-04-05.
 */
public class Game implements Serializable {

    private int id;
    private String name;
    private String desc;
    private String address;
    private String locationName;
    private double latitude;
    private double longitude;
    private int max_players;
    private int min_players;
    private int intensity;
    private double cost;
    private int gender;
    private String gameTime;
    private int userId;
    Sport sport;

    public Game(){}

    public Game(int id, String name, String desc, String address, String locationName, double latitude, double longitude,
                int min_players, int max_players, double cost, int intensity, int gender, int user_id, String gameTime, Sport sport) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.address = address;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.min_players = min_players;
        this.max_players = max_players;
        this.cost = cost;
        this.intensity = intensity;
        this.gender = gender;
        this.userId = user_id;
        this.gameTime = gameTime;
        this.sport = sport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMax_players() {
        return max_players;
    }

    public void setMax_players(int max_players) {
        this.max_players = max_players;
    }

    public int getMin_players() {
        return min_players;
    }

    public void setMin_players(int min_players) {
        this.min_players = min_players;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getlocationName() {
        return locationName;
    }

    public void setlocationName(String locationName) {
        this.locationName = locationName;
    }
}
