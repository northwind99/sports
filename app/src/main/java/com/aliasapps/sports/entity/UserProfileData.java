package com.aliasapps.sports.entity;

import java.util.List;

/**
 * Created by evanchen on 16-04-27.
 */
public class UserProfileData {

    private List<Game> pastGames;
    private List<Game> upcomingGames;
    private List<TrophyUserRelation> trophies;

    public UserProfileData(List<Game> pastGames, List<Game> upcomingGames, List<TrophyUserRelation> trophies) {
        this.pastGames = pastGames;
        this.upcomingGames = upcomingGames;
        this.trophies = trophies;
    }

    public List<Game> getPastGames() {
        return pastGames;
    }

    public void setPastGames(List<Game> pastGames) {
        this.pastGames = pastGames;
    }

    public List<Game> getUpcomingGames() {
        return upcomingGames;
    }

    public void setUpcomingGames(List<Game> upcomingGames) {
        this.upcomingGames = upcomingGames;
    }

    public List<TrophyUserRelation> getTrophies() {
        return trophies;
    }

    public void setTrophies(List<TrophyUserRelation> trophies) {
        this.trophies = trophies;
    }
}
