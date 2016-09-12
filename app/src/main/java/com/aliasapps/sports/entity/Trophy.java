package com.aliasapps.sports.entity;

/**
 * Created by evanchen on 16-04-27.
 */
public class Trophy {

    private int id;
    private String desc;
    private String name;
    private String icon;

    public Trophy(int id, String desc, String name, String icon) {
        this.id = id;
        this.desc = desc;
        this.name = name;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
