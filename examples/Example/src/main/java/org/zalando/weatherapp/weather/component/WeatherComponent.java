package org.zalando.weatherapp.weather.component;

import dagger.Subcomponent;
import org.zalando.weatherapp.weather.ui.activity.WeatherActivity;
import org.zalando.weatherapp.weather.ui.activity.WeatherForecastActivity;

@Subcomponent
public interface WeatherComponent {

  void inject(WeatherActivity activity);

  void inject(WeatherForecastActivity activity);
}
