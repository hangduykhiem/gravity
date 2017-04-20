package org.zalando.weatherapp.weather.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import org.zalando.core.ui.adapter.BaseAdapter;
import org.zalando.weatherapp.R;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import org.zalando.weatherapp.weather.ui.adapter.viewholder.WeatherForecastViewHolder;

public class WeatherForecastAdapter extends
    BaseAdapter<WeatherForecast, WeatherForecastViewHolder> {

  public WeatherForecastAdapter(final List<WeatherForecast> weatherForecasts) {
    super(weatherForecasts);
  }

  @Override
  public WeatherForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // Get LayoutInflater to inflate view
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    // Inflate the custom layout
    final View contactView = inflater.inflate(R.layout.weather_forecast_list_item, parent, false);
    return new WeatherForecastViewHolder(contactView);
  }
}
