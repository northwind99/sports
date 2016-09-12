package com.aliasapps.sports.entity;

import java.io.Serializable;

/**
 * Created by evanchen on 16-03-30.
 */
public class Sport implements Serializable{

    private int id;
    private String name;
    private String icon;
    private String map_icon;

    public Sport(){}

    public Sport(int id, String name, String icon, String map_icon){
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.map_icon = map_icon;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMap_icon() {
        return map_icon;
    }

    public void setMap_icon(String map_icon) {
        this.map_icon = map_icon;
    }

}
