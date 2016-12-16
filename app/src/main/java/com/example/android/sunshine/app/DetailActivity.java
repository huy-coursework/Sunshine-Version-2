/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailActivity extends ActionBarActivity {

    public static final int FORECAST_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsActivity.startSettingsActivity(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String FORECAST_HASH_TAG = " #SunshineApp";

        private TextView textForecast;
        private ShareActionProvider mShareActionProvider;
        private String forecast;
        private Uri uri;

        private static final String[] FORECAST_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
        };
        static final int COL_WEATHER_DATE = 0;
        static final int COL_WEATHER_DESC = 1;
        static final int COL_WEATHER_MAX_TEMP = 2;
        static final int COL_WEATHER_MIN_TEMP = 3;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            textForecast = (TextView) rootView.findViewById(R.id.text_forecast);

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                forecast = intent.getDataString();
                uri = Uri.parse(forecast);
                textForecast.setText(forecast);
            }

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.detailfragment, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (mShareActionProvider != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, forecast + FORECAST_HASH_TAG);
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
            return new CursorLoader(
                    getActivity(),
                    uri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (textForecast != null) {
                data.moveToFirst();
                String strDate = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
                String strDescription = data.getString(COL_WEATHER_DESC);
                String strMaxTemp = Utility.formatTemperature(
                        data.getDouble(COL_WEATHER_MAX_TEMP),
                        Utility.isMetric(getActivity()));
                String strMinTemp = Utility.formatTemperature(
                        data.getDouble(COL_WEATHER_MIN_TEMP),
                        Utility.isMetric(getActivity()));

                String displayText = String.format(
                        "%s - %s - %s/%s",
                        strDate,
                        strDescription,
                        strMaxTemp,
                        strMinTemp);
                textForecast.setText(displayText);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
