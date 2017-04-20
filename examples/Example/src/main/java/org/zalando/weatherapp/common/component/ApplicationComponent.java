package org.zalando.weatherapp.common.component;

import dagger.Component;
import javax.inject.Singleton;
import org.zalando.weatherapp.common.module.ApplicationModule;
import org.zalando.weatherapp.common.module.DataModule;
import org.zalando.weatherapp.common.module.UIModule;
import org.zalando.weatherapp.weather.component.WeatherComponent;

@Component(modules = {
    ApplicationModule.class,
    DataModule.class,
    UIModule.class
})
@Singleton
public interface ApplicationComponent {

    /* application specific component or dependencies can be injected here */

  WeatherComponent injectWeatherComponent();
}
