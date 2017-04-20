package org.zalando.weatherapp.weather.data.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class WeatherForecastListData extends WeatherData {

  public Location city;

  public ArrayList<ForecastData> list;

  public static class Location {

    public String name;
  }

  public static class ForecastData {

    @SerializedName("dt")
    public long timestamp;
    public Temperature temp;
    public ArrayList<Weather> weather;
  }

  public static class Temperature {

    public float min;
    public float max;
  }
}
