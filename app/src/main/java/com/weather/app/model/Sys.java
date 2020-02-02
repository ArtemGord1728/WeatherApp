package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

public class Sys
{
    public String country;

    @SerializedName("country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
