package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

public class Clouds
{
    private int all;

    @SerializedName("all")
    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }
}
