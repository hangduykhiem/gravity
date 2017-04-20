package org.zalando.weatherapp.common;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import org.zalando.core.ui.Navigator;
import org.zalando.weatherapp.weather.ui.activity.WeatherForecastActivity;

public class AppNavigator implements Navigator {

  public AppNavigator() {
      /* default */
  }

  /**
   * Launches {@link WeatherForecastActivity} with a default fragment
   * @param launchingActivity
   */
  public void navigateToForecast(@NonNull Activity launchingActivity) {
    final Intent intent = new Intent(launchingActivity, WeatherForecastActivity.class);
    launchingActivity.startActivity(intent);
  }
}
