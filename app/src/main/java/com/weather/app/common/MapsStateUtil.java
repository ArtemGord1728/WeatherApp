package com.weather.app.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weather.app.model.ListInfo;

import java.util.ArrayList;


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

        map.addMarker(new MarkerOptions()
                .position(target)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, AppConstants.DEFAULT_ZOOM));
    }

    public static void getSavedMarkers(GoogleMap map, Context context)
    {
        TinyDB tinyDB = new TinyDB(context);
        ArrayList<ListInfo> list = tinyDB.getListObject("key", ListInfo.class);

        for (int i = 0; i < list.size(); i++) {
            double temperature = list.get(i).getMain().getTemp();
            double windSpeed = list.get(i).getWind().getSpeed();
            double lat = list.get(i).getCoord().getLatitude();
            double lng = list.get(i).getCoord().getLongitude();
            String nameLocality = list.get(i).getName();
            String nameCountry = list.get(i).getSys().getCountry();

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(nameLocality + ", " + nameCountry)
                    .snippet(temperature + " °C, " + windSpeed + " м/с")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }
}