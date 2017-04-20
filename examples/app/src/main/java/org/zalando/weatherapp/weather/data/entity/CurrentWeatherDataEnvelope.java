package org.zalando.weatherapp.weather.data.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Data structure for current weather results returned by the web service.
 */
public class CurrentWeatherDataEnvelope extends WeatherDataEnvelope {

  @SerializedName("name")
  public String locationName;

  @SerializedName("dt")
  public long timestamp;

  public ArrayList<Weather> weather;

  public Main main;

  public static class Main {

    public float temp;
    public float temp_min;
    public float temp_max;
  }
}
