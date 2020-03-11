package com.weather.app.network;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.weather.app.common.AppConstants;

public class GPSTracker extends Service implements LocationListener {

    boolean isGPSEnabled = false;

    boolean isNetworkEnabled = false;

    boolean isGPSTrackingEnabled = false;

    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

    protected LocationManager locationManager;

    private String provider_info;

    public GPSTracker() {
        getLocation();
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true;


                provider_info = LocationManager.GPS_PROVIDER;

            } else if (isNetworkEnabled) {
                this.isGPSTrackingEnabled = true;


                provider_info = LocationManager.NETWORK_PROVIDER;

            }


            if (!provider_info.isEmpty()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {

                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION};

                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            permissions, AppConstants.REQUEST_CODE);
                    return;
                }
                locationManager.requestLocationUpdates(
                        provider_info,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider_info);
                    updateGPSCoordinates();
                }
            }
        }
        catch (Exception e)
        {
            return;
        }
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }


    public void getDeviceLocation(boolean isLocationPermissionsGranted,
                                  GoogleMap map, FusedLocationProviderClient fusedLocationProviderClient,
                                  Activity activity)
    {
        if(isLocationPermissionsGranted)
        {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            Location currentLocation = task.getResult();
                            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, AppConstants.DEFAULT_ZOOM));
                        }
                        else {
                            Toast.makeText(activity, "Ошибка! \n Попробуйте повторить попытку позже", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
