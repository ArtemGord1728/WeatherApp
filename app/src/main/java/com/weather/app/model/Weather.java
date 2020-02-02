package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

public class Weather
{
    private int id;
    private String main;
    private String description;
    private String icon;


    @SerializedName("id")
    public int getId() {
        return id;
    }


    @SerializedName("main")
    public String getMain() {
        return main;
    }


    @SerializedName("icon")
    public String getIcon() {
        return icon;
    }


    @SerializedName("description")
    public String getDescription() {
        return description;
    }



    public void setMain(String main) {
        this.main = main;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(int id) {
        this.id = id;
    }
}
