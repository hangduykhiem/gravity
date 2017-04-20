package org.zalando.weatherapp.weather.domain;

import android.location.Address;
import android.text.TextUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.zalando.core.domain.BaseService;
import org.zalando.weatherapp.weather.data.AddressRepository;
import org.zalando.weatherapp.weather.data.WeatherRepository;
import org.zalando.weatherapp.weather.domain.entity.CurrentWeather;
import org.zalando.weatherapp.weather.domain.entity.WeatherForecast;
import timber.log.Timber;

public class WeatherService extends BaseService {

  private final WeatherRepository weatherRepository;
  private final AddressRepository addressRepository;

  @Inject
  public WeatherService(WeatherRepository weatherRepository, AddressRepository addressRepository) {
    this.weatherRepository = weatherRepository;
    this.addressRepository = addressRepository;
  }

  public Observable<CurrentWeather> fetchCurrentWeather() {
    return fetchCurrentLocationAddress()
        .flatMap(new Function<String, ObservableSource<? extends CurrentWeather>>() {
                   @Override
                   public ObservableSource<? extends CurrentWeather> apply(@NonNull String address)
                       throws Exception {
                     return weatherRepository.fetchCurrentWeather(address).toObservable();
                   }
                 }
        ).compose(this.<CurrentWeather>applySchedulersToObservable());
  }

  public Observable<List<WeatherForecast>> fetchWeatherForecasts() {
    return fetchCurrentLocationAddress()
        .flatMap(new Function<String, ObservableSource<? extends List<WeatherForecast>>>() {
                   @Override
                   public ObservableSource<? extends List<WeatherForecast>>
                   apply(@NonNull String address) throws Exception {
                     return weatherRepository.fetchWeatherForecasts(address).toObservable();
                   }
                 }
        ).compose(this.<List<WeatherForecast>>applySchedulersToObservable());
  }

  private Observable<String> fetchCurrentLocationAddress() {
    return addressRepository.fetchCurrentAddress().map(
        new Function<Address, String>() {
          @Override
          public String apply(@NonNull Address address) throws Exception {

            Timber.d("address " + address);

            List<String> addressFragments = new ArrayList<>(2);
            addressFragments.add(address.getSubAdminArea());
            addressFragments.add(address.getCountryCode());

            return TextUtils.join(",", addressFragments);
          }
        });
  }
}
