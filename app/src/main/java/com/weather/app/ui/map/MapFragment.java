package com.weather.app.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weather.app.R;
import com.weather.app.common.MapsStateUtil;
import com.weather.app.common.SharedPrefUtils;
import com.weather.app.common.TinyDB;
import com.weather.app.model.ListInfo;
import com.weather.app.model.ListWeatherResults;
import com.weather.app.network.GPSTracker;
import com.weather.app.network.OpenWeatherAPI;
import com.weather.app.network.RetrofitClient;
import com.weather.app.common.AppConstants;

import java.util.ArrayList;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MarkerOptions markerOptions;
    private LatLng currentLatLng;

    private SharedPreferences preferences;

    private GPSTracker gpsTracker;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Retrofit retrofit;

    private OpenWeatherAPI openWeatherAPI;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        gpsTracker = new GPSTracker(getActivity());
        preferences = getActivity().getSharedPreferences(AppConstants.PREFERENCES, Context.MODE_PRIVATE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        retrofit = RetrofitClient.getRetrofit();
        openWeatherAPI = retrofit.create(OpenWeatherAPI.class);


        return inflater.inflate(R.layout.fragment_map,
                container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
    }

    public boolean isLocationPermissionsGranted()
    {
        boolean isGranted = false;
        if(ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                isGranted = true;
            }
        }

        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case AppConstants.REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                        }
                    }
                }
                break;
        }
    }

    private void initMap()
    {
        SupportMapFragment mapFragment =
                (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.main_map);

        if(mapFragment != null)
        {
            mapFragment.setRetainInstance(true);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MapsStateUtil.saveMapState(map, preferences);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;

        if(isLocationPermissionsGranted())
        {
            map.setMaxZoomPreference(AppConstants.DEFAULT_ZOOM);
            map.setMyLocationEnabled(true);
        }
        else
        {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            ActivityCompat.requestPermissions(getActivity(),
                    permissions, AppConstants.REQUEST_CODE);
        }

        map.setOnMapClickListener(latLng ->
        {
            currentLatLng = latLng;

            map.clear();

            map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(currentLatLng));

            SharedPreferences.Editor editor = preferences.edit();
            SharedPrefUtils.getInstance().putDouble(editor, AppConstants.SAVE_FLAG_1, currentLatLng.latitude);
            SharedPrefUtils.getInstance().putDouble(editor, AppConstants.SAVE_FLAG_2, currentLatLng.longitude);
            editor.apply();

            openWeatherAPI.getWeatherResultForTowns(String.valueOf(currentLatLng.latitude),
                    String.valueOf(currentLatLng.longitude), AppConstants.COUNT_TOWNS, AppConstants.APP_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<ListWeatherResults>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(ListWeatherResults listWeatherResults) {
                            ArrayList<ListInfo> listWeatherInfo = listWeatherResults.getList();

                            for (int i = 0; i < listWeatherInfo.size(); i++)
                            {
                                double temperature = listWeatherInfo.get(i).getMain().getTemp();
                                double windSpeed = listWeatherInfo.get(i).getWind().getSpeed();
                                double lat = listWeatherInfo.get(i).getCoord().getLatitude();
                                double lng = listWeatherInfo.get(i).getCoord().getLongitude();
                                String nameLocality = listWeatherInfo.get(i).getName();
                                String nameCountry = listWeatherInfo.get(i).getSys().getCountry();

                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lng))
                                        .title(nameLocality + ", " + nameCountry)
                                        .snippet(temperature + " °C, " + windSpeed + " м/с")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                                TinyDB tinyDB = new TinyDB(getActivity());
                                tinyDB.putListObject("key", listWeatherInfo);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
        MapsStateUtil.getSavedCurrentPosition(preferences, map);
        MapsStateUtil.getSavedMarkers(map, getActivity());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(map != null) {
            outState.putParcelable(AppConstants.CAMERA_POS, map.getCameraPosition());
            super.onSaveInstanceState(outState);
        }
    }
}