package org.zalando.weatherapp.weather.domain.entity;

import org.zalando.core.utils.EqualUtils;

public class WeatherForecast {

  private final String locationName;
  private final long timestamp;
  private final String description;
  private final float minimumTemperature;
  private final float maximumTemperature;

  public WeatherForecast(final String locationName,
      final long timestamp,
      final String description,
      final float minimumTemperature,
      final float maximumTemperature) {

    this.locationName = locationName;
    this.timestamp = timestamp;
    this.minimumTemperature = minimumTemperature;
    this.maximumTemperature = maximumTemperature;
    this.description = description;
  }

  public String getLocationName() {
    return locationName;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getDescription() {
    return description;
  }

  public float getMinimumTemperature() {
    return minimumTemperature;
  }

  public float getMaximumTemperature() {
    return maximumTemperature;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WeatherForecast that = (WeatherForecast) o;
    return timestamp == that.timestamp &&
        Float.compare(that.minimumTemperature, minimumTemperature) == 0 &&
        Float.compare(that.maximumTemperature, maximumTemperature) == 0 &&
        EqualUtils.areEqual(locationName, that.locationName) &&
        EqualUtils.areEqual(description, that.description);
  }

  @Override
  public int hashCode() {
    int result = locationName != null ? locationName.hashCode() : 0;
    result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result =
        31 * result + (minimumTemperature != +0.0f ? Float.floatToIntBits(minimumTemperature) : 0);
    result =
        31 * result + (maximumTemperature != +0.0f ? Float.floatToIntBits(maximumTemperature) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "WeatherForecast{" +
        "locationName='" + locationName + '\'' +
        ", timestamp=" + timestamp +
        ", description='" + description + '\'' +
        ", minimumTemperature=" + minimumTemperature +
        ", maximumTemperature=" + maximumTemperature +
        '}';
  }
}


