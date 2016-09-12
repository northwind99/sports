package com.aliasapps.sports.entity;

/**
 * Created by evanchen on 16-04-22.
 */
public class Attendee {

    private int id;
    private User user;
    private int gameId;
    private int status;

    public Attendee(int id, User user, int gameId, int status) {
        this.id = id;
        this.user = user;
        this.gameId = gameId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
