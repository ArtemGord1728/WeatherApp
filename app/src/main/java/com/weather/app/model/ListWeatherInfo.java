package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListWeatherInfo
{
    private int id ;
    private String name;
    private Coordinate coord;
    private Main main;
    private int dt;
    private Wind wind;
    private Sys sys;
    private Object rain;
    private Object snow;
    private Clouds clouds;
    private List<Weather> weather;


    @SerializedName("id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SerializedName("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("coord")
    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    @SerializedName("main")
    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    @SerializedName("dt")
    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    @SerializedName("wind")
    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    @SerializedName("sys")
    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    @SerializedName("rain")
    public Object getRain() {
        return rain;
    }

    public void setRain(Object rain) {
        this.rain = rain;
    }

    @SerializedName("snow")
    public Object getSnow() {
        return snow;
    }

    public void setSnow(Object snow) {
        this.snow = snow;
    }

    @SerializedName("clouds")
    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    @SerializedName("weather")
    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
