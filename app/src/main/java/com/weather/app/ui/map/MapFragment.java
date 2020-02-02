package com.weather.app.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weather.app.R;
import com.weather.app.model.ListInfo;
import com.weather.app.model.ListWeatherResults;
import com.weather.app.network.GPSTracker;
import com.weather.app.network.OpenWeatherAPI;
import com.weather.app.network.RetrofitClient;
import com.weather.app.common.AppConstants;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MarkerOptions markerOptions;
    private LatLng currentLatLng;

    private GPSTracker gpsTracker;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean isLocationPermissionsGranted;
    private static final int REQUEST_CODE = 1024;
    private final int countOfTowns = 20;
    private Retrofit retrofit;

    private OpenWeatherAPI openWeatherAPI;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        gpsTracker = new GPSTracker(getActivity());

        getLocationPermissions();
        if(isLocationPermissionsGranted)
        {
            initMap();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        retrofit = RetrofitClient.getRetrofit();
        openWeatherAPI = retrofit.create(OpenWeatherAPI.class);

        return root;
    }

    public void getLocationPermissions()
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                isLocationPermissionsGranted = true;
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions, REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions, REQUEST_CODE);
            Toast.makeText(getActivity(), "Go to Список", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            isLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    isLocationPermissionsGranted = true;
                    initMap();
                }
                break;
        }
    }

    private void initMap()
    {
        SupportMapFragment mapFragment =
                (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.main_map);


        if(mapFragment == null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_map, mapFragment).commit();
        }
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);
    }



    @SuppressLint("CheckResult")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        gpsTracker.getDeviceLocation(isLocationPermissionsGranted,
                map, fusedLocationProviderClient, getActivity());

        markerOptions = new MarkerOptions();

        map.setOnMapClickListener(latLng -> {
            currentLatLng = latLng;

            map.clear();

            map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));

            map.addMarker(markerOptions.position(currentLatLng));

            SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(AppConstants.SAVE_FLAG_1, Double.doubleToRawLongBits(currentLatLng.latitude));
            editor.putLong(AppConstants.SAVE_FLAG_2, Double.doubleToRawLongBits(currentLatLng.longitude));
            editor.apply();

            openWeatherAPI.getWeatherResultForTowns(String.valueOf(currentLatLng.latitude),
                    String.valueOf(currentLatLng.longitude), countOfTowns, AppConstants.APP_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ListWeatherResults>() {
                        @Override
                        public void onNext(ListWeatherResults listWeatherResults) {
                            ArrayList<ListInfo> listWeatherInfo = listWeatherResults.getList();

                            for (int i = 0; i < listWeatherInfo.size(); i++)
                            {
                                double temperature = listWeatherInfo.get(i).getMain().getTemp();
                                double windSpeed = listWeatherInfo.get(i).getWind().getSpeed();
                                double lat = listWeatherInfo.get(i).getCoord().getLatitude();
                                double lng = listWeatherInfo.get(i).getCoord().getLongitude();
                                String nameLocality = listWeatherInfo.get(i).getName();
                                String nameCountry = listWeatherInfo.get(i).getSys().getCountry();

                                map.addMarker(markerOptions.position(new LatLng(lat, lng))
                                        .title(nameLocality + ", " + nameCountry)
                                        .snippet(temperature + " °C, " + windSpeed + " м/с"));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            // Empty
                        }
                    });
        });
    }
}