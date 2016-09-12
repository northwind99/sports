package com.aliasapps.sports.entity;

import java.io.Serializable;

/**
 * Created by evanchen on 16-05-13.
 */
public class Conversation implements Serializable {
    private int id;
    User creator;
    User to;
    Message lastMessage;

    public Conversation(int id, User creator, User to, Message lastMessage) {

        this.id = id;
        this.creator = creator;
        this.to = to;
        this.lastMessage = lastMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

}
