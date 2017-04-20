package org.zalando.weatherapp.weather.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.zalando.core.ui.adapter.BaseAdapter;
import java.util.List;
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
    // get the context
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    // Inflate the custom layout
    View contactView = inflater.inflate(R.layout.weather_forecast_list_item, parent, false);
    // Return a new holder instance
    return new WeatherForecastViewHolder(contactView);
  }
}
