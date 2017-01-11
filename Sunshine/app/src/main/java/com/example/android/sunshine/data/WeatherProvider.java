package com.example.android.sunshine.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.SunshineDateUtils;

/**
 * Created by MY on 2017-01-11.
 */

public class WeatherProvider extends ContentProvider {
    WeatherDbHelper mOpenHelper;

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());

        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();

        int code = sUriMatcher.match(uri);
        switch(code) {
            case CODE_WEATHER:
                sqLiteDatabase.beginTransaction();

                int insertedNum = 0;
                try {
                    for(ContentValues value : values) {
                        long weatherDate = value.getAsLong(WeatherEntry.COLUMN_DATE);

                        if(!SunshineDateUtils.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("It is not normal date form.");
                        }

                        long _id = sqLiteDatabase.insert(WeatherEntry.TABLE_NAME, null, value);
                        if(_id != -1) {
                            insertedNum++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();

                } finally {
                    sqLiteDatabase.endTransaction();
                }

                if(insertedNum > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return insertedNum;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "We are not implementing insert in Sunshine. Use bulkInsert instead");
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mOpenHelper.getReadableDatabase();

        Cursor retCursor;

        int code = sUriMatcher.match(uri);
        switch(code) {
            case CODE_WEATHER:
                retCursor = sqLiteDatabase.query(WeatherEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case CODE_WEATHER_WITH_DATE:
                String date = uri.getLastPathSegment();
                String mSelection = WeatherEntry.COLUMN_DATE + "=?";
                String[] mSelectionArgs = new String[]{date};

                retCursor = sqLiteDatabase.query(WeatherEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("No match Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        int code = sUriMatcher.match(uri);
        int itemDel;

        if(selection == null)
            selection = "1";

        switch(code) {
            case CODE_WEATHER:
                itemDel = sqLiteDatabase.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("No match Uri: " + uri);
        }

        if(itemDel != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return itemDel;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in Sunshine");
    }

    @Override
    public String getType(Uri uri) {
        throw new RuntimeException("We are not implementing getType in Sunshine.");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
