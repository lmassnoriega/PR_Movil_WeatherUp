package com.uninorte.weatherup;

import java.io.Serializable;

/**
 * Created by Luis on 24/04/2015.
 */
public class WeatherReport implements Serializable {

    public final String urlbase = "http://openweathermap.org/img/w/";
    public String CityName;
    public String ImgReport;
    public int Humidity;
    public double Temp;
    public String ForecastDesc;

    public WeatherReport(){

    }

    public WeatherReport(String cityName, String imgReport, int humidity, double temp, String forecastDesc) {
        CityName = cityName;
        ImgReport = urlbase+imgReport+".png";
        Humidity = humidity;
        Temp = temp;
        ForecastDesc = forecastDesc;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getImgReport() {
        return ImgReport;
    }

    public void setImgReport(String imgReport) {
        ImgReport = urlbase+imgReport+".png";
    }

    public int getHumidity() {
        return Humidity;
    }

    public void setHumidity(int humidity) {
        Humidity = humidity;
    }

    public double getTemp() {
        return Temp;
    }

    public void setTemp(double temp) {
        Temp = temp;
    }

    public String getForecastDesc() {
        return ForecastDesc;
    }

    public void setForecastDesc(String forecastDesc) {
        ForecastDesc = forecastDesc;
    }
}
