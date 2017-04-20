package org.zalando.weatherapp.weather.data;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.zalando.core.data.BaseRepository;
import org.zalando.weatherapp.weather.data.entity.CurrentWeatherDataEnvelope;
import org.zalando.weatherapp.weather.data.entity.WeatherForecastListDataEnvelope;
import org.zalando.weatherapp.weather.data.network.OpenWeatherRestApi;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;

public class WeatherRepository extends BaseRepository {

  private final OpenWeatherRestApi openWeatherRestApi;

  @Inject
  public WeatherRepository(OpenWeatherRestApi weatherRestApi) {
    openWeatherRestApi = weatherRestApi;
  }

  public Single<CurrentWeather> fetchCurrentWeather(final String address) {
    return openWeatherRestApi.fetchCurrentWeather(address)
        .map(new Function<CurrentWeatherDataEnvelope, CurrentWeather>() {
          @Override
          public CurrentWeather apply(
              @NonNull CurrentWeatherDataEnvelope data) throws Exception {
            return new CurrentWeather(data.locationName,
                data.timestamp,
                data.weather.get(0).description,
                data.main.temp,
                data.main.temp_min,
                data.main.temp_max);
          }
        });
  }

  public Single<List<WeatherForecast>> fetchWeatherForecasts(final String address) {
    return openWeatherRestApi.fetchWeatherForecasts(address)
        .map(new Function<WeatherForecastListDataEnvelope, List<WeatherForecast>>() {
          @Override
          public List<WeatherForecast> apply(@NonNull WeatherForecastListDataEnvelope listData)
              throws Exception {

            // Parse the result and build a list of WeatherForecast objects.
            final ArrayList<WeatherForecast> weatherForecasts =
                new ArrayList<>(listData.list.size());
            for (WeatherForecastListDataEnvelope.ForecastDataEnvelope data : listData.list) {
              final WeatherForecast weatherForecast = new WeatherForecast(listData.city.name,
                  data.timestamp, data.weather.get(0).description, data.temp.min, data.temp.max);
              weatherForecasts.add(weatherForecast);
            }
            return weatherForecasts;
          }
        });
  }
}
