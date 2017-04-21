package org.zalando.weatherapp.weather.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import javax.inject.Inject;
import org.zalando.core.ui.activity.BaseActivity;
import org.zalando.core.ui.presenter.BasePresenter;
import org.zalando.weatherapp.R;
import org.zalando.weatherapp.WeatherApp;
import org.zalando.weatherapp.common.AppNavigator;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;
import org.zalando.weatherapp.weather.ui.presenter.WeatherActivityPresenter;
import org.zalando.weatherapp.weather.ui.view.WeatherActivityView;
import org.zalando.weatherapp.weather.util.TemperatureFormatter;

public class WeatherActivity extends BaseActivity implements WeatherActivityView {

  public static final int PERMISSIONS_RQ_LOCATION = 9999;

  @BindView(R.id.weather_text_city)
  TextView textCity;

  @BindView(R.id.weather_text_lat_lon)
  TextView textLatLon;

  @Inject
  WeatherActivityPresenter presenter;

  @Override
  protected void injectDependencies() {
    ((WeatherApp) getApplication()).getWeatherComponent().inject(this);
  }

  @Override
  protected int getSubActivityLayoutId() {
    return R.layout.weather_activity;
  }

  @NonNull
  @Override
  protected BasePresenter getPresenter() {
    return presenter;
  }

  @Override
  public void showCurrentWeather(CurrentWeather currentWeather) {
    textCity.setText(currentWeather.getLocationName() + " (" + TemperatureFormatter
        .format(currentWeather.getTemperature()) + ")");
  }

  @Override
  public void initView(@NonNull Bundle initBundle) {
    // ask for location permission
    askLocationPermission();
  }

  @OnClick(R.id.weather_forecast_button)
  public void onClickShowForecast() {
    // use AppNavigator to navigate to Forecast Activity
    ((AppNavigator) navigator.get()).navigateToForecast(this);
  }

  protected void askLocationPermission() {
    if (ContextCompat
        .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_RQ_LOCATION);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    switch (requestCode) {
      case PERMISSIONS_RQ_LOCATION: {
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // after getting permissions, initialise presenter again
          presenter.initialise(new Bundle());
        }
      }
    }
  }

  @Override
  public void showError(Throwable throwable) {
    Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
  }
}
