package org.zalando.weatherapp.weather.data.network;

import io.reactivex.Single;
import org.zalando.weatherapp.common.Constants;
import org.zalando.weatherapp.weather.data.entity.CurrentWeatherData;
import org.zalando.weatherapp.weather.data.entity.WeatherForecastListData;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit Rest Api definition for Weather API
 */
public interface OpenWeatherRestApi {

  // address as {city name},{country code} or {city name}
  @GET("weather?units=metric&apikey=" + Constants.API_KEY)
  Single<CurrentWeatherData> fetchCurrentWeather(@Query("q") String address);

  @GET("forecast/daily?units=metric&cnt=7&apikey=" + Constants.API_KEY)
  Single<WeatherForecastListData> fetchWeatherForecasts(@Query("q") String address);
}
