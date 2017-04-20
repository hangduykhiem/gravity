package org.zalando.weatherapp.weather.component;

import dagger.Subcomponent;
import org.zalando.weatherapp.weather.ui.activity.WeatherActivity;

@Subcomponent
public interface WeatherComponent {

  void inject(WeatherActivity activity);
}
