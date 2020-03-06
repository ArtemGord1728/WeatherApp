package com.weather.app.ui.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weather.app.common.AppConstants;
import com.weather.app.common.SharedPrefUtils;
import com.weather.app.model.ListInfo;
import com.weather.app.model.ListWeatherResults;
import com.weather.app.network.GPSTracker;

import java.util.ArrayList;

import io.reactivex.Single;

public class MapsStateUtil
{

    public static void saveMapState(GoogleMap mapMie /*, ArrayList<ListInfo> listWeatherInfo*/, SharedPreferences mapStatePrefs) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        CameraPosition position = mapMie.getCameraPosition();

        SharedPrefUtils.getInstance().putDouble(editor, AppConstants.SAVE_FLAG_1, position.target.latitude);
        SharedPrefUtils.getInstance().putDouble(editor, AppConstants.SAVE_FLAG_2, position.target.longitude);

        editor.apply();
    }

    public static void getSavedCurrentPosition(SharedPreferences mapStatePrefs, GoogleMap map)
    {
        double latitude = SharedPrefUtils.getInstance().getDouble(mapStatePrefs, AppConstants.SAVE_FLAG_1, 0.0f);
        double longitude = SharedPrefUtils.getInstance().getDouble(mapStatePrefs, AppConstants.SAVE_FLAG_2, 0.0f);

        map.clear();

        LatLng target = new LatLng(latitude, longitude);

        map.animateCamera(CameraUpdateFactory.newLatLng(target));

        map.addMarker(new MarkerOptions().position(target));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, AppConstants.DEFAULT_ZOOM));
    }

    public static void getSavedMarkers(SharedPreferences preferences, GoogleMap map)
    {
        double temperature = SharedPrefUtils.getInstance().getDouble(preferences, AppConstants.TEMPERATURE, 0.0f);
        double windSpeed = SharedPrefUtils.getInstance().getDouble(preferences, AppConstants.WIND_SPEED, 0.0f);
        double lat = SharedPrefUtils.getInstance().getDouble(preferences, AppConstants.LAT, 0.0f);
        double lng = SharedPrefUtils.getInstance().getDouble(preferences, AppConstants.LNG, 0.0f);
        String nameLocality = SharedPrefUtils.getInstance().getString(preferences, AppConstants.NAME_LOCAL, "");
        String nameCountry = SharedPrefUtils.getInstance().getString(preferences, AppConstants.NAME_COUNTRY, "");

        map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                .title(nameLocality + ", " + nameCountry)
                .snippet(temperature + " °C, " + windSpeed + " м/с"));

    }

//    public static CameraPosition getSavedCameraPosition() {
//        double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
//        if (latitude == 0) {
//            return null;
//        }
//        double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
//        LatLng target = new LatLng(latitude, longitude);
//
//        float zoom = mapStatePrefs.getFloat(ZOOM, 0);
//        float bearing = mapStatePrefs.getFloat(BEARING, 0);
//        float tilt = mapStatePrefs.getFloat(TILT, 0);
//
       // CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
//        return position;
//    }

//    public static int getSavedMapType() {
//        return mapStatePrefs.getInt(MAPTYPE, GoogleMap.MAP_TYPE_NORMAL);
//    }
}
