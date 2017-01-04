package com.example.android.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    TextView mDetailTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailTextView = (TextView) findViewById(R.id.weather_detail);

        Intent intent = getIntent();
        String weatherDetail = intent.getStringExtra("weather");
        mDetailTextView.setText(weatherDetail);
    }
}
