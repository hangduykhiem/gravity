package org.zalando.weatherapp.weather.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import butterknife.BindView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.zalando.core.ui.fragment.BaseFragment;
import org.zalando.core.ui.presenter.BasePresenter;
import org.zalando.weatherapp.R;
import org.zalando.weatherapp.WeatherApp;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import org.zalando.weatherapp.weather.ui.adapter.WeatherForecastAdapter;
import org.zalando.weatherapp.weather.ui.presenter.WeatherForecastFragmentPresenter;
import org.zalando.weatherapp.weather.ui.view.WeatherForecastFragmentView;

public class WeatherForecastFragment extends BaseFragment implements WeatherForecastFragmentView {

  @BindView(R.id.weather_recycler)
  RecyclerView recyclerView;

  @Inject
  WeatherForecastFragmentPresenter presenter;

  private WeatherForecastAdapter recyclerAdapter;

  @Override
  protected int getSubFragmentLayoutId() {
    return R.layout.weather_forecast_fragment;
  }

  @NonNull
  @Override
  protected BasePresenter getPresenter() {
    return presenter;
  }

  @Override
  protected void injectDependencies() {
    ((WeatherApp) getApplication()).getWeatherComponent().inject(this);
  }

  @Override
  public void initView(@NonNull Bundle bundle) {
    /* do something with bundle or some initialization */
    // initialize recyclerView
    recyclerAdapter = new WeatherForecastAdapter(new ArrayList<WeatherForecast>());
    // Attach the adapter to the recyclerview to populate items
    recyclerView.setAdapter(recyclerAdapter);
    // Set layout manager to position the items
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  @Override
  public void showWeatherForecasts(List<WeatherForecast> weatherForecasts) {
    if (weatherForecasts != null) {
      recyclerAdapter.setItems(weatherForecasts);
    }
  }

  @Override
  public void showError(Throwable throwable) {
    Toast.makeText(getContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
  }
}
