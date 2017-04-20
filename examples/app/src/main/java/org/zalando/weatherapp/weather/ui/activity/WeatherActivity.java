package org.zalando.weatherapp.weather.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.zalando.core.ui.activity.BaseActivity;
import org.zalando.core.ui.presenter.BasePresenter;
import org.zalando.weatherapp.R;
import org.zalando.weatherapp.WeatherApp;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import org.zalando.weatherapp.weather.ui.adapter.WeatherForecastAdapter;
import org.zalando.weatherapp.weather.ui.presenter.WeatherActivityPresenter;
import org.zalando.weatherapp.weather.ui.view.WeatherActivityView;
import org.zalando.weatherapp.weather.util.TemperatureFormatter;

public class WeatherActivity extends BaseActivity implements WeatherActivityView {

  public static final int PERMISSIONS_RQ_LOCATION = 9999;

  @BindView(R.id.weather_text_city)
  TextView textCity;

  @BindView(R.id.weather_text_lat_lon)
  TextView textLatLon;

  @BindView(R.id.weather_recycler)
  RecyclerView recyclerView;

  @Inject
  WeatherActivityPresenter presenter;

  private WeatherForecastAdapter recyclerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initView(savedInstanceState);

    askLocationPermission();
  }

  @Override
  protected void injectDependencies() {
    ((WeatherApp) getApplication()).getWeatherComponent().inject(this);
  }

  @Override
  protected int getSubActivityLayoutId() {
    return R.layout.activity_main;
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
  public void showWeatherForecasts(List<WeatherForecast> weatherForecasts) {
    if (weatherForecasts != null) {
      recyclerAdapter.setItems(weatherForecasts);
    }
  }

  @Override
  public void initView(@NonNull Bundle initBundle) {
    recyclerAdapter = new WeatherForecastAdapter(new ArrayList<WeatherForecast>());
    // Attach the adapter to the recyclerview to populate items
    recyclerView.setAdapter(recyclerAdapter);
    // Set layout manager to position the items
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
