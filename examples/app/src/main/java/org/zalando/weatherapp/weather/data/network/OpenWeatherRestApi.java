package org.zalando.weatherapp.weather.data.network;

import io.reactivex.Single;
import org.zalando.weatherapp.common.Constants;
import org.zalando.weatherapp.weather.data.entity.CurrentWeatherDataEnvelope;
import org.zalando.weatherapp.weather.data.entity.WeatherForecastListDataEnvelope;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit Rest Api definition for Weather API
 */
public interface OpenWeatherRestApi {

  // http://openweathermap.org/current
  // address as {city name},{country code} or {city name}
  @GET("weather?units=metric&apikey=" + Constants.API_KEY)
  Single<CurrentWeatherDataEnvelope> fetchCurrentWeather(@Query("q") String address);

  @GET("forecast/daily?units=metric&cnt=7&apikey=" + Constants.API_KEY)
  Single<WeatherForecastListDataEnvelope> fetchWeatherForecasts(@Query("q") String address);
}
