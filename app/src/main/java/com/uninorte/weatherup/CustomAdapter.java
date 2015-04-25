package com.uninorte.weatherup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Luis on 24/04/2015.
 */
public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context context;
    private List<WeatherReport> listEntries;

    public CustomAdapter(Context context, List<WeatherReport> listEntries) {
        this.context = context;
        this.listEntries = listEntries;
    }

    @Override
    public int getCount() {
        return listEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return listEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherReport entry = listEntries.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, null);
        }

        TextView for_desc = (TextView) convertView.findViewById(R.id.textView2);
        TextView Humidity_info = (TextView) convertView.findViewById(R.id.textView3);
        TextView temp = (TextView) convertView.findViewById(R.id.textView);

        for_desc.setText(String.valueOf(entry.getForecastDesc()));
        Humidity_info.setText(String.valueOf(entry.getHumidity())+"%");
        temp.setText(String.valueOf(entry.getTemp())+" °C");

        new DownloadImageTask((ImageView) convertView.findViewById(R.id.imageView)).execute(entry.getImgReport());

        convertView.setTag(entry);
        return convertView;
    }

    @Override
    public void onClick(View v) {
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