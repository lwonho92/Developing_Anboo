package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

public class SunshineSyncUtils {
    private static boolean sInitialized;

    synchronized public static void initialize(final Context context) {

        if(sInitialized)
            return;
        sInitialized = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Uri uri = WeatherEntry.CONTENT_URI;

                String[] selectedColumns = {WeatherEntry._ID};
                String selection = WeatherEntry.getSqlSelectForTodayOnwards();

                Cursor cursor = context.getContentResolver().query(uri, selectedColumns, selection, null, null);

                if(cursor == null || cursor.getCount() == 0)
                    startImmediateSync(context);
                cursor.close();

                return null;
            }
        }.execute();
    }


    public static void startImmediateSync(final Context context) {
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}