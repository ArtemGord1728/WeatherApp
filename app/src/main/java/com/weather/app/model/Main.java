package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

public class Main
{
    private double temp;
    private int pressure;
    private int humidity;
    private double feels_like;
    private double temp_min;
    private double temp_max;


    @SerializedName("feels_like")
    public double getFeels_like() {
        return feels_like;
    }


    @SerializedName("temp")
    public double getTemp() {
        return temp - 273.15;
    }


    @SerializedName("pressure")
    public int getPressure() {
        return pressure;
    }


    @SerializedName("humidity")
    public int getHumidity() {
        return humidity;
    }


    @SerializedName("temp_min")
    public double getTempMin() {
        return temp_min;
    }


    @SerializedName("temp_max")
    public double getTempMax() {
        return temp_max;
    }



    public void setFeels_like(double feels_like) {
        this.feels_like = feels_like;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setTempMin(double temp_min) {
        this.temp_min = temp_min;
    }

    public void setTempMax(double temp_max) {
        this.temp_max = temp_max;
    }
}
