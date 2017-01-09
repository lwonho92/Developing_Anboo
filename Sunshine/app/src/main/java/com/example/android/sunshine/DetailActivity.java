package com.example.android.sunshine;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    private TextView mDetailTextView;
    private String weatherDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailTextView = (TextView) findViewById(R.id.weather_detail);

        Intent intent = getIntent();

        if(intent != null) {
            if(intent.hasExtra(Intent.EXTRA_TEXT)) {
                weatherDetail = intent.getStringExtra(Intent.EXTRA_TEXT);
                mDetailTextView.setText(weatherDetail);
            }
        }
    }

    private Intent createIntentShare() {
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(weatherDetail + "#SunSine")
                .getIntent();

        return intent;
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
