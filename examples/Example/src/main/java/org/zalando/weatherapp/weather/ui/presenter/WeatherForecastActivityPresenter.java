package org.zalando.weatherapp.weather.ui.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import io.reactivex.functions.Consumer;
import java.util.List;
import javax.inject.Inject;
import org.zalando.core.domain.helper.DisposableHelper;
import org.zalando.core.ui.presenter.BasePresenter;
import org.zalando.weatherapp.weather.domain.WeatherService;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import org.zalando.weatherapp.weather.ui.view.WeatherForecastActivityView;
import timber.log.Timber;

public class WeatherForecastActivityPresenter extends BasePresenter<WeatherForecastActivityView> {

  private WeatherService weatherService;

  @Inject
  public WeatherForecastActivityPresenter(DisposableHelper disposableHelper,
      WeatherService weatherService) {
    super(disposableHelper);
    this.weatherService = weatherService;
  }

  @Override
  public void initialise(@NonNull Bundle state) {
    super.initialise(state);
    loadWeatherForecasts();
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
