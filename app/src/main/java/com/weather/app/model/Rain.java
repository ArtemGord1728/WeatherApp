package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

public class Rain
{
    private double name__3h;

    @SerializedName("3h")
    public double getName__3h() {
        return name__3h;
    }

    public void setName__3h(double name__3h) {
        this.name__3h = name__3h;
    }

}
