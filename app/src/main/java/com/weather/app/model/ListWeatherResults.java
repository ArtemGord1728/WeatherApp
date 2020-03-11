package com.weather.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListWeatherResults
{
    private String message;
    private String cod;
    private int count;
    private ArrayList<ListWeatherInfo> list;


    @SerializedName("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("count")
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @SerializedName("list")
    public ArrayList<ListWeatherInfo> getList() {
        return list;
    }

    public void setList(ArrayList<ListWeatherInfo> list) {
        this.list = list;
    }

    @SerializedName("cod")
    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }
}
