package org.zalando.weatherapp.weather.util;

/**
 * Extension of {@link Exception} to manage null or empty data.
 */
public class EmptyDataException extends Exception {

  public EmptyDataException() {
    super("Data is null or empty");
  }

  public EmptyDataException(String message) {
    super(message);
  }
}
