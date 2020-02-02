package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

public class Wind
{
    private double speed;
    private int deg;

    @SerializedName("speed")
    public double getSpeed() {
        return speed;
    }

    @SerializedName("deg")
    public int getDeg() {
        return deg;
    }


    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }
}
