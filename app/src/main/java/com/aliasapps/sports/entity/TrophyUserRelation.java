package com.aliasapps.sports.entity;

/**
 * Created by evanchen on 16-04-27.
 */
public class TrophyUserRelation {

    private int id;
    private int userId;
    Trophy trophy;

    public TrophyUserRelation(int id, int userId, Trophy trophy) {
        this.id = id;
        this.userId = userId;
        this.trophy = trophy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Trophy getTrophy() {
        return trophy;
    }

    public void setTrophy(Trophy trophy) {
        this.trophy = trophy;
    }
}
