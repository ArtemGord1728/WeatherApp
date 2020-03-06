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

    public SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public double getDouble(final SharedPreferences sharedPreferences, String key, final double defaultValue) {
        return Double.longBitsToDouble(sharedPreferences.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public SharedPreferences.Editor putString(final SharedPreferences.Editor editor, String key, final String value) {
        return editor.putString(key, value);
    }

    public String getString(final SharedPreferences sharedPreferences, String key, final String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
}
