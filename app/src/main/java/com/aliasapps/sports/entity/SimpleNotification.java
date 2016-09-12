package com.aliasapps.sports.entity;

/**
 * Created by evanchen on 16-05-19.
 */
public class SimpleNotification {

    private int id;
    private String text;
    private String icon;
    private int seen;
    User user;
    Game game;
    private long created;

    public SimpleNotification(int id, String text, String icon, int seen, User user, Game game, long created) {
        this.id = id;
        this.text = text;
        this.icon = icon;
        this.seen = seen;
        this.user = user;
        this.game = game;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
