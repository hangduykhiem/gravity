package org.zalando.weatherapp;

import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import org.zalando.weatherapp.common.component.ApplicationComponent;
import org.zalando.weatherapp.common.component.ApplicationComponentInitializer;
import org.zalando.weatherapp.weather.component.WeatherComponent;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class WeatherApp extends MultiDexApplication {

  @Nullable
  private ApplicationComponent component;

  @Nullable
  private WeatherComponent weatherComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    /* initialize global level instances or configurations such as logging or analytics */

    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    }
  }

  public ApplicationComponent getCommonComponent() {
    if (component == null) {
      component = ApplicationComponentInitializer.initComponent(this);
    }
    return component;
  }

  public WeatherComponent getWeatherComponent() {
    if (weatherComponent == null) {
      weatherComponent = getCommonComponent().injectWeatherComponent();
    }
    return weatherComponent;
  }
}
