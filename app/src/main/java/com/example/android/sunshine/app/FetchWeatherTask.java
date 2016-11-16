package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    public static final String QUERY_PARAM_Q = "q";
    public static final String QUERY_PARAM_MODE = "mode";
    public static final String QUERY_PARAM_UNITS = "units";
    public static final String QUERY_PARAM_CNT = "cnt";
    public static final String QUERY_PARAM_APPID = "APPID";
    public static final String NUMBER_OF_DAYS = "7";
    public static final String UNITS = "metrics";
    public static final String OUTPUT_FORMAT = "json";

    @Override
    protected Void doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("api.openweathermap.org")
                    .path("data/2.5/forecast/daily")
                    .appendQueryParameter(QUERY_PARAM_Q, params[0])
                    .appendQueryParameter(QUERY_PARAM_MODE, OUTPUT_FORMAT)
                    .appendQueryParameter(QUERY_PARAM_UNITS, UNITS)
                    .appendQueryParameter(QUERY_PARAM_CNT, NUMBER_OF_DAYS)
                    .appendQueryParameter(QUERY_PARAM_APPID, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();
            String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
            URL url = new URL(uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                forecastJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            forecastJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.v(LOG_TAG, "Received data from OWM: " + forecastJsonStr);
        return null;
    }
}
