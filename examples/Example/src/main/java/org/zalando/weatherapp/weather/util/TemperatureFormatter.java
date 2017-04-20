package org.zalando.weatherapp.weather.util;

public class TemperatureFormatter {

  public static String format(float temperature) {
    return String.valueOf(Math.round(temperature)) + "°C";
  }
}
