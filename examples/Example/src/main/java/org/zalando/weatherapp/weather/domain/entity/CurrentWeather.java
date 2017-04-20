package org.zalando.weatherapp.weather.domain.entity;

public class CurrentWeather extends WeatherForecast {

  private final float temperature;

  public CurrentWeather(final String locationName,
      final long timestamp,
      final String description,
      final float temperature,
      final float minimumTemperature,
      final float maximumTemperature) {

    super(locationName, timestamp, description, minimumTemperature, maximumTemperature);
    this.temperature = temperature;
  }

  public float getTemperature() {
    return temperature;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CurrentWeather that = (CurrentWeather) o;
    return Float.compare(that.temperature, temperature) == 0;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (temperature != +0.0f ? Float.floatToIntBits(temperature) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CurrentWeather{" +
        "temperature=" + temperature +
        '}';
  }
}
