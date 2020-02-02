package com.weather.app.common;

import android.content.SharedPreferences;

public class SharedPrefUtils {
    public static SharedPrefUtils instance;

    public synchronized static SharedPrefUtils getInstance() {
        if(instance == null) {
            instance = new SharedPrefUtils();
        }

        return instance;
    }

    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public double getDouble(final SharedPreferences sharedPreferences, String key, final double defaultValue) {
        return Double.longBitsToDouble(sharedPreferences.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
