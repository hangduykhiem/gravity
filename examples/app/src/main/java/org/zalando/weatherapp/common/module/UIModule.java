package org.zalando.weatherapp.common.module;

import dagger.Module;
import dagger.Provides;
import org.zalando.core.ui.Navigator;
import org.zalando.weatherapp.common.AppNavigator;

@Module
public class UIModule {

  @Provides
  public Navigator provideNavigator() {
    return new AppNavigator();
  }
}
