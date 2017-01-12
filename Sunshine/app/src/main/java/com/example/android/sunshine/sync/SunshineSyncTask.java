package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask {

    synchronized public static void syncWeather(Context context) {

        URL weatherRequestUrl = NetworkUtils.getUrl(context);

        try {
            String jsonWeatherResponse = NetworkUtils
                    .getResponseFromHttpUrl(weatherRequestUrl);

            ContentValues[] simpleJsonWeatherData = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            if(simpleJsonWeatherData != null && simpleJsonWeatherData.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();

                contentResolver.delete(WeatherEntry.CONTENT_URI, null, null);

                contentResolver.bulkInsert(WeatherEntry.CONTENT_URI, simpleJsonWeatherData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}