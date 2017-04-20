package org.zalando.weatherapp.weather.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import javax.inject.Inject;
import org.zalando.core.ui.activity.BaseActivity;
import org.zalando.core.ui.presenter.BasePresenter;
import org.zalando.core.ui.presenter.StubPresenter;
import org.zalando.core.ui.view.StubView;
import org.zalando.weatherapp.R;
import org.zalando.weatherapp.WeatherApp;
import org.zalando.weatherapp.weather.ui.fragment.WeatherForecastFragment;

public class WeatherForecastActivity extends BaseActivity implements StubView {

  public static final int PERMISSIONS_RQ_LOCATION = 9999;

  @Inject
  StubPresenter presenter;

  @Override
  protected void injectDependencies() {
    ((WeatherApp) getApplication()).getWeatherComponent().inject(this);
  }

  @Override
  protected int getSubActivityLayoutId() {
    return R.layout.weather_forecast_activity;
  }

  @NonNull
  @Override
  protected BasePresenter getPresenter() {
    return presenter;
  }

  @Override
  public void initView(@NonNull Bundle initBundle) {
    /* do something with bundle or initialization */
    askLocationPermission();
    // set default fragment
    setFragment(R.id.fragment_container, new WeatherForecastFragment());
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
}
