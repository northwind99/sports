package com.aliasapps.sports.entity;

import java.io.Serializable;

/**
 * Created by evanchen on 16-05-13.
 */
public class Message implements Serializable{
    private int id;
    private int conversationId;
    private int creatorId;
    private String message;
    private int seen;
    private long created;

    public Message(int id, int conversationId, int creatorId, String message, int seen, long created) {
        this.id = id;
        this.conversationId = conversationId;
        this.creatorId = creatorId;
        this.message = message;
        this.seen = seen;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
