package com.weather.app.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.weather.app.common.AppConstants;
import com.weather.app.common.TinyDB;
import com.weather.app.network.GPSTracker;
import com.weather.app.network.OpenWeatherAPI;
import com.weather.app.network.RetrofitClient;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Objects;

import retrofit2.Retrofit;


public class MapFragment extends Fragment implements LocationListener
{
    //private SharedPreferences preferences;

    private GPSTracker gpsTracker;
    private TinyDB tinyDB;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Retrofit retrofit;
    private OpenWeatherAPI openWeatherAPI;

    private MapView mapView;
    private SharedPreferences mPrefs;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private CopyrightOverlay mCopyrightOverlay;

    private static final String PREFS_NAME = "org.andnav.osm.prefs";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        gpsTracker = new GPSTracker();
        //preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        mapView = new MapView(inflater.getContext());
        mapView.setBuiltInZoomControls(true);
        mapView.setDestroyMode(false);
        mapView.setTag("mapview");
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setOnGenericMotionListener((v, event) -> {
            if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                        if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                            mapView.getController().zoomOut();
                        else {
                            IGeoPoint iGeoPoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                            mapView.getController().animateTo(iGeoPoint);
                            mapView.getController().zoomIn();
                        }
                        return true;
                }
            }
            return false;
        });

        retrofit = RetrofitClient.getRetrofit();
        openWeatherAPI = retrofit.create(OpenWeatherAPI.class);

        tinyDB = new TinyDB(getActivity());

        return mapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(this.mLocationOverlay);



        //Mini map
        mMinimapOverlay = new MinimapOverlay(context, mapView.getTileRequestCompleteHandler());
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);
        mapView.getOverlays().add(this.mMinimapOverlay);



        //Copyright overlay
        mCopyrightOverlay = new CopyrightOverlay(context);
        //i hate this very much, but it seems as if certain versions of android and/or
        //device types handle screen offsets differently
        mapView.getOverlays().add(this.mCopyrightOverlay);




        //On screen compass
        mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                mapView);
        mCompassOverlay.enableCompass();
        mapView.getOverlays().add(this.mCompassOverlay);


        //map scale
        mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(this.mScaleBarOverlay);



        //support for map rotation
        mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(this.mRotationGestureOverlay);


        //needed for pinch zooms
        mapView.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        mapView.setTilesScaledToDpi(true);

        //the rest of this is restoring the last map location the user looked at
        final float zoomLevel = mPrefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1);
        mapView.getController().setZoom(zoomLevel);
        final float orientation = mPrefs.getFloat(PREFS_ORIENTATION, 0);
        mapView.setMapOrientation(orientation, false);
        final String latitudeString = mPrefs.getString(PREFS_LATITUDE_STRING, "1.0");
        final String longitudeString = mPrefs.getString(PREFS_LONGITUDE_STRING, "1.0");
        final double latitude = Double.valueOf(latitudeString);
        final double longitude = Double.valueOf(longitudeString);
        mapView.setExpectedCenter(new GeoPoint(latitude, longitude));

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        //save the current location
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mapView.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, mapView.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(mapView.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(mapView.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) mapView.getZoomLevelDouble());
        edit.apply();

        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }

        mapView.onResume();
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

    @Override
    public void onStop() {
        super.onStop();
        //MapsStateUtil.saveMapState(map, preferences);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//
//        if(isLocationPermissionsGranted())
//        {
//            map.setMaxZoomPreference(AppConstants.DEFAULT_ZOOM);
//            map.setMyLocationEnabled(true);
//            gpsTracker.getDeviceLocation(map, fusedLocationProviderClient, getActivity());
//        }
//        else
//        {
//            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION};
//
//            ActivityCompat.requestPermissions(getActivity(),
//                    permissions, AppConstants.REQUEST_CODE);
//
//        }
//
//        showDefaultLocation();
//
//        map.setOnMapClickListener(latLng ->
//        {
//            currentLatLng = latLng;
//
//            map.clear();
//
//            map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
//
//            map.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                    .position(currentLatLng));
//
//            tinyDB.putDouble(SAVE_FLAG_1, currentLatLng.latitude);
//            tinyDB.putDouble(SAVE_FLAG_2, currentLatLng.longitude);
//
//            openWeatherAPI.getWeatherResultForTowns(String.valueOf(currentLatLng.latitude),
//                    String.valueOf(currentLatLng.longitude), AppConstants.COUNT_TOWNS, AppConstants.APP_ID)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new SingleObserver<ListWeatherResults>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//                        }
//
//                        @Override
//                        public void onSuccess(ListWeatherResults listWeatherResults) {
//                            ArrayList<ListWeatherInfo> listWeatherInfo = listWeatherResults.getList();
//
//                            for (int i = 0; i < listWeatherInfo.size(); i++)
//                            {
//                                double temperature = listWeatherInfo.get(i).getMain().getTemp();
//                                double windSpeed = listWeatherInfo.get(i).getWind().getSpeed();
//                                double lat = listWeatherInfo.get(i).getCoord().getLatitude();
//                                double lng = listWeatherInfo.get(i).getCoord().getLongitude();
//                                String nameLocality = listWeatherInfo.get(i).getName();
//                                String nameCountry = listWeatherInfo.get(i).getSys().getCountry();
//
//                                map.addMarker(new MarkerOptions()
//                                        .position(new LatLng(lat, lng))
//                                        .title(nameLocality + ", " + nameCountry)
//                                        .snippet(temperature + Objects.requireNonNull(getResources()).getString(R.string.celciy) + ", " + windSpeed
//                                                + Objects.requireNonNull(getResources()).getString(R.string.metre_sec))
//                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//
//
//                                tinyDB.putListObject("key", listWeatherInfo);
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Toast.makeText(getActivity(), "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//        });
////        MapsStateUtil.showSavedCurrentPosition(preferences, map);
//        MapsStateUtil.showSavedMarkers(map, getActivity());
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showDefaultLocation()
    {
        final double defaultLat = 47.2262556;
        final double defaultLng = 39.6964441;
    }

    @Override
    public void onLocationChanged(Location location)
    {

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