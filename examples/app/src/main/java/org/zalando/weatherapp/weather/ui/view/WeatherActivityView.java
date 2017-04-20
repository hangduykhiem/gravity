package org.zalando.weatherapp.weather.ui.view;

import org.zalando.core.ui.view.BaseView;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import java.util.List;

public interface WeatherActivityView extends BaseView {

    void showCurrentWeather(CurrentWeather currentWeather);

    void showWeatherForecasts(List<WeatherForecast> weatherForecasts);

    void showError(Throwable throwable);
}
