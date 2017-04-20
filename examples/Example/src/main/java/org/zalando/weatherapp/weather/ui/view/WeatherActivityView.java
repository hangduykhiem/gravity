package org.zalando.weatherapp.weather.ui.view;

import org.zalando.core.ui.view.BaseView;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;

public interface WeatherActivityView extends BaseView {

    void showCurrentWeather(CurrentWeather currentWeather);

    void showError(Throwable throwable);
}
