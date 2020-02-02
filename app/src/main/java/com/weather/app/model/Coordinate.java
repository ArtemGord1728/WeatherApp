package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

public class Coordinate
{
    private double lon;
    private double lat;

    @SerializedName("lon")
    public double getLongitude() {
        return lon;
    }

    @SerializedName("lat")
    public double getLatitude() {
        return lat;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }
}
