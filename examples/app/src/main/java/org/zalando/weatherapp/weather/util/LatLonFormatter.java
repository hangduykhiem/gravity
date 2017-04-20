package org.zalando.weatherapp.weather.util;

import android.location.Location;
import java.util.Locale;

public class LatLonFormatter {

  public static String format(Location location) {
    return format(location.getLatitude()) + ", " + format(location.getLongitude());
  }

  public static String format(double value) {
    return String.format(Locale.US, "%.2f", value);
  }
}
