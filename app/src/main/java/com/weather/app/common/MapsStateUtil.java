package com.weather.app.common;

public class MapsStateUtil
{
    private static final String SAVE_FLAG_1 = "FLAG_1";
    private static final String SAVE_FLAG_2 = "FLAG_2";

//    public static void saveMapState(GoogleMap mapMie, SharedPreferences mapStatePrefs) {
//        SharedPreferences.Editor editor = mapStatePrefs.edit();
//
//        SharedPrefUtils.getInstance().putDouble(editor, SAVE_FLAG_1, position.target.latitude);
//        SharedPrefUtils.getInstance().putDouble(editor, SAVE_FLAG_2, position.target.longitude);
//
//        editor.apply();
//    }

//    public static void showSavedCurrentPosition(SharedPreferences mapStatePrefs, GoogleMap map) {
//        double latitude = SharedPrefUtils.getInstance().getDouble(mapStatePrefs, SAVE_FLAG_1, 0.0f);
//        double longitude = SharedPrefUtils.getInstance().getDouble(mapStatePrefs, SAVE_FLAG_2, 0.0f);
//
//        map.clear();
//
//        LatLng target = new LatLng(latitude, longitude);
//
//        map.animateCamera(CameraUpdateFactory.newLatLng(target));
//
//        map.addMarker(new MarkerOptions()
//                .position(target)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, AppConstants.DEFAULT_ZOOM));
//    }

//    public static void showSavedMarkers(GoogleMap map, Context context) {
//        TinyDB tinyDB = new TinyDB(context);
//        ArrayList<ListWeatherInfo> list = tinyDB.getListObject("key", ListWeatherInfo.class);
//
//        for (int i = 0; i < list.size(); i++) {
//            double temperature = list.get(i).getMain().getTemp();
//            double windSpeed = list.get(i).getWind().getSpeed();
//            double lat = list.get(i).getCoord().getLatitude();
//            double lng = list.get(i).getCoord().getLongitude();
//            String nameLocality = list.get(i).getName();
//            String nameCountry = list.get(i).getSys().getCountry();
//
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(lat, lng))
//                    .title(nameLocality + ", " + nameCountry)
//                    .snippet(temperature + " °C, " + windSpeed + " м/с")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//        }
}