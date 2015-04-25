package com.uninorte.weatherup;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog pDialog;
    private static String url = "http://api.openweathermap.org/data/2.5/weather?q=Barranquilla,co&units=metric";
    private static String url1 = "http://api.openweathermap.org/data/2.5/forecast/daily?id=3689147&units=metric";
    JSONArray predictions = null;
    ArrayList<WeatherReport> Forecast;
    WeatherReport CurrentConds;
    private ListView listView;
    private TextView cityText;
    private TextView tempText;
    private TextView Descript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Forecast = new ArrayList<>();
        CurrentConds = new WeatherReport();
        listView = (ListView) findViewById(R.id.listView);
        cityText = (TextView) findViewById(R.id.today_CityName);
        tempText = (TextView) findViewById(R.id.today_CityTemp);
        Descript = (TextView) findViewById(R.id.today_desc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isNetworkAvaible = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isNetworkAvaible = true;
            Toast.makeText(this, "Network is available ", Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(this, "Network not available ", Toast.LENGTH_LONG)
                    .show();
        }
        return isNetworkAvaible;
    }

    public void DoMagic(View view){

        if (isNetworkAvailable()){
            new GetCurrentForecast().execute();
            new GetWeekForecast().execute();
        }
    }



    private class GetCurrentForecast extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Downloading Current Forecast...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String cityName = jsonObj.getString("name");
                    String country = jsonObj.getJSONObject("sys").getString("country");

                    JSONArray todaystats = jsonObj.getJSONArray("weather");
                    JSONObject mainStats = jsonObj.getJSONObject("main");

                    Double Temperature = Double.parseDouble(mainStats.getString("temp"));
                    int Humid = Integer.parseInt(mainStats.getString("humidity"));

                    String image = todaystats.getJSONObject(0).getString("icon");
                    String ForDesc = todaystats.getJSONObject(0).getString("main");
                    CurrentConds.setForecastDesc(ForDesc);
                    CurrentConds.setImgReport(image);
                    CurrentConds.setTemp(Temperature);
                    CurrentConds.setHumidity(Humid);
                    CurrentConds.setCityName(cityName+", "+country);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Descript.setText(CurrentConds.getForecastDesc());
            cityText.setText(CurrentConds.getCityName());
            tempText.setText(Math.round(CurrentConds.getTemp())+" °C");
            new DownloadImageTask((ImageView) findViewById(R.id.today_weather)).execute(CurrentConds.getImgReport());
        }
    }


    private class GetWeekForecast extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog.setMessage("Getting Weekly Predictions...");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String cityName = jsonObj.getJSONObject("city").getString("name");
                    predictions = jsonObj.getJSONArray("list");
                    Log.d("Response length: ", "> " + predictions.length());

                    for (int i = 0; i < predictions.length(); i++) {

                        WeatherReport dataEntry = new WeatherReport();
                        dataEntry.setCityName(cityName);

                        String humidity = predictions.getJSONObject(i).getString("humidity");
                        dataEntry.setHumidity(Integer.parseInt(humidity));

                        String temp = predictions.getJSONObject(i).getJSONObject("temp").getString("day");
                        dataEntry.setTemp(Double.parseDouble(temp));

                        JSONObject Weather = predictions.getJSONObject(i).getJSONArray("weather").getJSONObject(0);

                        dataEntry.setForecastDesc(Weather.getString("main"));
                        dataEntry.setImgReport(Weather.getString("icon"));

                        Forecast.add(dataEntry);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            CustomAdapter adapter = new CustomAdapter(MainActivity.this, Forecast);
            listView.setAdapter(adapter);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
