package com.uninorte.weatherup;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog pDialog;
    //private static String url = "http://api.androidhive.info/contacts/";
    private static String url = "http://api.openweathermap.org/data/2.5/weather?q=Barranquilla,co&units=metric";
    private static String url1 = "http://api.openweathermap.org/data/2.5/forecast/daily?id=3689147&units=metric";
    JSONArray predictions = null;
    ArrayList<WeatherReport> Forecast;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Forecast = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

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
            //new GetData().execute();
            new GetWeekForecast().execute();
        }
    }




    private class GetWeekForecast extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Downloading Data...");
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
                    predictions = jsonObj.getJSONArray("list");
                    Log.d("Response length: ", "> " + predictions.length());

                    for (int i = 0; i < predictions.length(); i++) {

                        WeatherReport dataEntry = new WeatherReport();
                        dataEntry.setCityName(cityName);

                        String humidity = predictions.getJSONObject(i).getJSONObject("humidity").toString();
                        dataEntry.setHumidity(Integer.parseInt(humidity));

                        String temp = predictions.getJSONObject(i).getJSONObject("temp").getJSONObject("day").toString();
                        dataEntry.setTemp(Double.parseDouble(temp));

                        JSONObject Weather = predictions.getJSONObject(i).getJSONArray("weather").getJSONObject(0);

                        dataEntry.setForecastDesc(Weather.getJSONObject("main").toString());
                        dataEntry.setImgReport(Weather.getJSONObject("icon").toString());

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
}
