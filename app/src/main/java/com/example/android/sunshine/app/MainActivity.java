package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_map:
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(this);
                String locationPref = sharedPref.getString(
                        getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(
                        Uri.parse("geo:0,0?")
                                .buildUpon()
                                .appendQueryParameter("q", locationPref)
                                .build());
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                return true;
            case R.id.action_settings:
                SettingsActivity.startSettingsActivity(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
