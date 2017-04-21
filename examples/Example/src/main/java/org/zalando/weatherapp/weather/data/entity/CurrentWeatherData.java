package org.zalando.weatherapp.weather.data.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class CurrentWeatherData extends WeatherData {

  @SerializedName("name")
  public String locationName;

  @SerializedName("dt")
  public long timestamp;

  public ArrayList<Weather> weather;

  @SerializedName("main")
  public MainData main;

  public static class MainData {

    public float temp;
    public float temp_min;
    public float temp_max;
  }
}
