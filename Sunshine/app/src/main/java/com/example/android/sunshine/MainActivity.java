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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private TextView mErrorTextView;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    public MainActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mErrorTextView = (TextView) findViewById(R.id.error_message);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        loadWeatherData();
    }

    public void loadWeatherData() {
        showWeatherDataView();
        String preferredWeatherLocation = SunshinePreferences.getPreferredWeatherLocation(this);

        new NetworkRequest().execute(preferredWeatherLocation);
    }

    public void showWeatherDataView() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    // TODO (9) Create a method called showErrorMessage that will hide the weather data and show the error message
    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    Toast toast;

    @Override
    public void access(String str) {
        Context context = this;
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        startActivity(intent);
    }

    public class NetworkRequest extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            URL url = NetworkUtils.buildUrl(params[0]);

            String[] data = null;

            try {
                String str = NetworkUtils.getResponseFromHttpUrl(url);
                data = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, str);

                return data;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] s) {
            mProgressBar.setVisibility(View.INVISIBLE);

            if(s != null) {
                /*for(String str : s) {
                    mWeatherTextView.append(str + "\n\n\n");
                }*/
                mForecastAdapter.setWeatherData(s);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        if(selectedItem == R.id.action_refresh ) {
            mForecastAdapter = null;
            mForecastAdapter = new ForecastAdapter(this);
            mRecyclerView.setAdapter(mForecastAdapter);
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}