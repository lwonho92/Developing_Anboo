package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighView;
    private TextView mLowView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    private String weatherDetail;
    private Uri mUri;

    public static final String[] DESIRED_COLUMNS = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_CONDITION = 1;
    public static final int INDEX_WEATHER_MAX_TEMP = 2;
    public static final int INDEX_WEATHER_MIN_TEMP = 3;
    public static final int INDEX_WEATHER_HUMIDITY = 4;
    public static final int INDEX_WEATHER_PRESSURE = 5;
    public static final int INDEX_WEATHER_WIND = 6;
    public static final int INDEX_WEATHER_DEGREES = 7;

    private static final int LOADER_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDateView = (TextView) findViewById(R.id.selected_date);
        mDescriptionView = (TextView) findViewById(R.id.selected_description);
        mHighView = (TextView) findViewById(R.id.selected_max_temp);
        mLowView = (TextView) findViewById(R.id.selected_min_temp);
        mHumidityView = (TextView) findViewById(R.id.selected_humidity);
        mWindView = (TextView) findViewById(R.id.selected_wind);
        mPressureView = (TextView) findViewById(R.id.selected_pressure);

        mUri = getIntent().getData();

        if(mUri == null)
            throw new NullPointerException("Uri's data that is null");

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private Intent createIntentShare() {
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(weatherDetail + "#SunSine")
                .getIntent();

        return intent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_ID:
                return new CursorLoader(this, mUri, DESIRED_COLUMNS, null, null, null);

            default:
                throw new RuntimeException("No Match Loader's Id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null) {
            return;
        }
        data.moveToFirst();

        long milliSeconds = data.getLong(INDEX_WEATHER_DATE);
        String milliSecondsString = SunshineDateUtils.getFriendlyDateString(this, milliSeconds, true);
        mDateView.setText(milliSecondsString);

        int weatherId = data.getInt(INDEX_WEATHER_CONDITION);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);
        mDescriptionView.setText(description);

        double maxTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String maxTempString = SunshineWeatherUtils.formatTemperature(this, maxTemp);
        mHighView.setText(maxTempString);

        double minTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String minTempString = SunshineWeatherUtils.formatTemperature(this, minTemp);
        mLowView.setText(minTempString);

        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        mHumidityView.setText(humidityString);

        float wind = data.getFloat(INDEX_WEATHER_WIND);
        float degrees = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this, wind, degrees);
        mWindView.setText(windString);

        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);
        mPressureView.setText(pressureString);

        weatherDetail = String.format("%s - %s - %s/%s",
                milliSecondsString, description, maxTempString, minTempString);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sharedata, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createIntentShare());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        switch(selectedItem) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
