package com.weather.app.network;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weather.app.common.AppConstants;
import com.weather.app.model.ListInfo;
import com.weather.app.model.ListWeatherResults;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static RetrofitClient instance;
    private static Retrofit client;

    public static RetrofitClient getInstance() {
        if (instance == null)
        {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public static Retrofit getRetrofit() {
        if(client == null)
        {
            client = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return client;
    }

}
