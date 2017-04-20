package org.zalando.weatherapp.weather.data;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.zalando.core.data.BaseRepository;
import org.zalando.weatherapp.weather.data.entity.CurrentWeatherData;
import org.zalando.weatherapp.weather.data.entity.WeatherForecastListData;
import org.zalando.weatherapp.weather.data.entity.WeatherForecastListData.ForecastData;
import org.zalando.weatherapp.weather.data.network.OpenWeatherRestApi;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;

/**
 * Extension of {@link BaseRepository} to perform REST api query to fetch current weather
 * and weather forecast.
 */
public class WeatherRepository extends BaseRepository {

  private final OpenWeatherRestApi openWeatherRestApi;

  @Inject
  public WeatherRepository(OpenWeatherRestApi weatherRestApi) {
    openWeatherRestApi = weatherRestApi;
  }

  public Single<CurrentWeather> fetchCurrentWeather(final String address) {
    return openWeatherRestApi.fetchCurrentWeather(address)
        // transform data from data layer entity to domain layer entity
        .map(new Function<CurrentWeatherData, CurrentWeather>() {
          @Override
          public CurrentWeather apply(
              @NonNull CurrentWeatherData data) throws Exception {
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
        // transform data from data layer entity to domain layer entity
        .map(new Function<WeatherForecastListData, List<WeatherForecast>>() {
          @Override
          public List<WeatherForecast> apply(@NonNull WeatherForecastListData listData)
              throws Exception {

            // Parse the result and build a list of WeatherForecast objects.
            final ArrayList<WeatherForecast> weatherForecasts =
                new ArrayList<>(listData.list.size());
            for (ForecastData data : listData.list) {
              final WeatherForecast weatherForecast = new WeatherForecast(listData.city.name,
                  data.timestamp, data.weather.get(0).description, data.temp.min, data.temp.max);
              weatherForecasts.add(weatherForecast);
            }
            return weatherForecasts;
          }
        });
  }
}
