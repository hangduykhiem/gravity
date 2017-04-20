package org.zalando.weatherapp.common.module;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class ApplicationModule {

  private final Application application;

  /**
   * Constructor
   *
   * @param application Application instance
   */
  public ApplicationModule(Application application) {
    this.application = application;
  }

  /**
   * Provides the {@link Application} instance for dependency injections
   *
   * @return {@link Application} instance
   */
  @Provides
  @Singleton
  public Application provideApplication() {
    return application;
  }

  /**
   * Provides the {@link Context} of the app for dependency injections
   *
   * @return {@link Context} of the app
   */
  @Provides
  @Singleton
  public Context provideApplicationContext() {
    return application.getApplicationContext();
  }
}
