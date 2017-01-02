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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mTextView = (TextView)findViewById(R.id.tv_weather_data);

        loadWeatherData();
    }

        public void loadWeatherData() {
        String preferredWeatherLocation = SunshinePreferences.getPreferredWeatherLocation(this);

        new NetworkRequest().execute(preferredWeatherLocation);
    }

    public class NetworkRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            URL url = NetworkUtils.buildUrl(params[0]);

            String str = null;

            try {
                str = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null && s != "")
                mTextView.setText(s);
        }
    }
}