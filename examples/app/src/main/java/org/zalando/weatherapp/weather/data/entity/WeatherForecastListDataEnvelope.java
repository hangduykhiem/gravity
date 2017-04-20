package org.zalando.weatherapp.weather.data.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class WeatherForecastListDataEnvelope extends WeatherDataEnvelope {

  public Location city;

  public ArrayList<ForecastDataEnvelope> list;

  public static class Location {

    public String name;
  }

  public static class ForecastDataEnvelope {

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
