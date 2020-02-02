package com.weather.app.network;

import com.weather.app.model.ListWeatherResults;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherAPI
{
    @GET("find")
    Observable<ListWeatherResults> getWeatherResultForTowns(@Query("lat") String latitude,
                                                            @Query("lon") String lon,
                                                            @Query("cnt") int cnt,
                                                            @Query("appid") String appId);
}
