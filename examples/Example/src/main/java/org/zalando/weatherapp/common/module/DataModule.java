package org.zalando.weatherapp.common.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import org.zalando.core.network.RestApiFactory;
import javax.inject.Singleton;
import org.zalando.weatherapp.weather.data.network.OpenWeatherRestApi;

@Module
public class DataModule {

  private String baseUrl;

  public DataModule(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Provides
  @Singleton
  Gson provideGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return gsonBuilder.create();
  }

  @Provides
  @Singleton
  OpenWeatherRestApi provideRetrofit(Gson gson) {
    return RestApiFactory.createApi(OpenWeatherRestApi.class,
        baseUrl,
        true
    );
  }
}
