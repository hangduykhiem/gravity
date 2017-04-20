package org.zalando.weatherapp.weather.ui.view;

import java.util.List;
import org.zalando.core.ui.view.BaseView;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;

public interface WeatherForecastActivityView extends BaseView {

    void showWeatherForecasts(List<WeatherForecast> weatherForecasts);

    void showError(Throwable throwable);
}
