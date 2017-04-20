package org.zalando.weatherapp.weather.ui.presenter;

import android.os.Bundle;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import java.util.List;
import javax.inject.Inject;
import org.zalando.core.domain.helper.DisposableHelper;
import org.zalando.core.ui.presenter.BasePresenter;
import org.zalando.weatherapp.weather.domain.WeatherService;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import org.zalando.weatherapp.weather.ui.view.WeatherActivityView;
import timber.log.Timber;

public class WeatherActivityPresenter extends BasePresenter<WeatherActivityView> {

  private WeatherService weatherService;

  @Inject
  public WeatherActivityPresenter(DisposableHelper disposableHelper,
      WeatherService weatherService) {
    super(disposableHelper);
    this.weatherService = weatherService;
  }

  @Override
  public void initialise(@android.support.annotation.NonNull Bundle state) {
    super.initialise(state);
    loadCurrentWeather();
    loadWeatherForecasts();
  }

  private void loadCurrentWeather() {
    disposableHelper.addDisposable(
        weatherService.fetchCurrentWeather()
            .subscribe(new Consumer<CurrentWeather>() {
              @Override
              public void accept(@NonNull CurrentWeather currentWeather) throws Exception {
                // Update UI with current weather.
                view.showCurrentWeather(currentWeather);
              }
            }, new Consumer<Throwable>() {
              @Override
              public void accept(@NonNull Throwable throwable) throws Exception {
                Timber.e(throwable, "loadCurrentWeather()");
                view.showError(throwable);
              }
            })
    );
  }

  private void loadWeatherForecasts() {
    disposableHelper.addDisposable(
        weatherService.fetchWeatherForecasts()
            .subscribe(new Consumer<List<WeatherForecast>>() {
              @Override
              public void accept(@NonNull List<WeatherForecast> weatherForecasts) throws Exception {
                // Update UI with weather forecast.
                view.showWeatherForecasts(weatherForecasts);
              }
            }, new Consumer<Throwable>() {
              @Override
              public void accept(@NonNull Throwable throwable) throws Exception {
                Timber.e(throwable, "loadWeatherForecasts()");
                view.showError(throwable);
              }
            })
    );
  }
}
