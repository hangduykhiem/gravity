package org.zalando.weatherapp.common.component;

import android.app.Application;
import org.zalando.weatherapp.common.module.ApplicationModule;
import org.zalando.weatherapp.common.module.DataModule;

public class ApplicationComponentInitializer {

  private static final String WEB_SERVICE_BASE_URL = "http://api.openweathermap.org/data/2.5/";

  /**
   * Avoid instances of the class, only static access to methods
   */
  private ApplicationComponentInitializer() {

  }

  /**
   * Initialises the {@link ApplicationComponent} taking into account the dependent Modules
   *
   * @param application {@link Application} instance
   * @return {@link ApplicationComponent} initialised
   */
  public static ApplicationComponent initComponent(Application application) {
    return DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(application))
        .dataModule(new DataModule(WEB_SERVICE_BASE_URL))
        .build();
  }
}
