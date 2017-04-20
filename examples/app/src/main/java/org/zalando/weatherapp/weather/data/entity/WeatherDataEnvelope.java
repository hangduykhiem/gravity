package org.zalando.weatherapp.weather.data.entity;

/**
 * Base class for results returned by the weather web service.
 */
public class WeatherDataEnvelope {

  public static class Weather {

    public String description;
  }
}
