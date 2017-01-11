/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.FakeDataUtils;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final int LOADER_ID = 0;

    public static final String[] DESIRED_COLUMNS = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_MAX_TEMP
    };

    public static final int INDEX_DATE = 0;
    public static final int INDEX_WEATHER_ID = 1;
    public static final int INDEX_MIN_TEMP = 2;
    public static final int INDEX_MAX_TEMP = 3;

    public MainActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        FakeDataUtils.insertFakeData(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this, this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        showLoading();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void showWeatherDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    // TODO (9) Create a method called showErrorMessage that will hide the weather data and show the error message
    public void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void access(String str) {
        Context context = this;
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, str);

        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch(id) {
            case LOADER_ID:
                Uri uri = WeatherEntry.CONTENT_URI;
                String selection = WeatherEntry.getSqlSelectForTodayOnwards();
                String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";

                return new CursorLoader(this, uri, DESIRED_COLUMNS, selection, null, sortOrder);

            default:
                throw new RuntimeException("No Match Loader's Id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.setCursor(data);
        if(mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if(data.getCount() != 0)
            showWeatherDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.setCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        switch(selectedItem) {
            case R.id.action_refresh:
                mForecastAdapter = null;
                mForecastAdapter = new ForecastAdapter(this, this);
                mRecyclerView.setAdapter(mForecastAdapter);
//            mForecastAdapter.setWeatherData(null);
//            loadWeatherData();
                getSupportLoaderManager().restartLoader(LOADER_ID, null, this);

                return true;
            case R.id.action_open_map:
                showMap();

                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMap() {
        String addr = SunshinePreferences.getPreferredWeatherLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addr);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}