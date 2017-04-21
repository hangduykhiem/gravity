package org.zalando.weatherapp.weather.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import org.zalando.core.ui.adapter.viewholder.BaseViewHolder;
import org.zalando.weatherapp.R;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import org.zalando.weatherapp.weather.util.DayFormatter;
import org.zalando.weatherapp.weather.util.TemperatureFormatter;

public class WeatherForecastViewHolder extends BaseViewHolder<WeatherForecast> {

  @BindView(R.id.day) TextView dayTextView;
  @BindView(R.id.description) TextView descriptionTextView;
  @BindView(R.id.maximum_temperature) TextView maximumTemperatureTextView;
  @BindView(R.id.minimum_temperature) TextView minimumTemperatureTextView;

  public WeatherForecastViewHolder(View itemView) {
    super(itemView);
  }

  @Override
  public void bindData(@NonNull WeatherForecast weatherForecast) {
    super.bindData(weatherForecast);

    final DayFormatter dayFormatter = new DayFormatter(itemView.getContext());
    final String day = dayFormatter.format(weatherForecast.getTimestamp());

    dayTextView.setText(day);
    descriptionTextView.setText(weatherForecast.getDescription());
    maximumTemperatureTextView
        .setText(TemperatureFormatter.format(weatherForecast.getMaximumTemperature()));
    minimumTemperatureTextView
        .setText(TemperatureFormatter.format(weatherForecast.getMinimumTemperature()));
  }
}
