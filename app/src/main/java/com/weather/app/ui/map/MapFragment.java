package com.weather.app.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weather.app.R;
import com.weather.app.common.MapsStateUtil;
import com.weather.app.common.TinyDB;
import com.weather.app.model.ListWeatherInfo;
import com.weather.app.model.ListWeatherResults;
import com.weather.app.network.OpenWeatherAPI;
import com.weather.app.network.RetrofitClient;
import com.weather.app.common.AppConstants;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private GoogleMap map;
    private LatLng currentLatLng;

    private SharedPreferences preferences;

    //private GPSTracker gpsTracker;
    private TinyDB tinyDB;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Retrofit retrofit;

    private OpenWeatherAPI openWeatherAPI;

    public static final String SAVE_FLAG_1 = "FLAG_1";
    public static final String SAVE_FLAG_2 = "FLAG_2";
    public static final String PREFERENCES = "pref";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //gpsTracker = new GPSTracker(getActivity());
        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        retrofit = RetrofitClient.getRetrofit();
        openWeatherAPI = retrofit.create(OpenWeatherAPI.class);

        tinyDB = new TinyDB(getActivity());

        return inflater.inflate(R.layout.fragment_map,
                container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
    }

    private boolean isLocationPermissionsGranted() {
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConstants.REQUEST_CODE) {
            for (int i = 0; grantResults.length > 0 && i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {

                }
            }
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
    public void onMapReady(GoogleMap googleMap) {
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

            tinyDB.putDouble(SAVE_FLAG_1, currentLatLng.latitude);
            tinyDB.putDouble(SAVE_FLAG_2, currentLatLng.longitude);

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
                            ArrayList<ListWeatherInfo> listWeatherInfo = listWeatherResults.getList();

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
                                        .snippet(temperature + Objects.requireNonNull(getResources()).getString(R.string.celciy) + ", " + windSpeed
                                                + Objects.requireNonNull(getResources()).getString(R.string.metre_sec))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                                tinyDB.putListObject("key", listWeatherInfo);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
        MapsStateUtil.showSavedCurrentPosition(preferences, map);
        MapsStateUtil.showSavedMarkers(map, getActivity());
    }
}