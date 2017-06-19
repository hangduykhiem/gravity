package org.zalando.core.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.zalando.core.utils.Preconditions;
import org.zalando.core.utils.ValidationUtils;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Factory class that helps building Rest Api interfaces using RetroFit.
 */
public final class RestApiFactory {

  /**
   * Private constructor to force static access to methods
   */
  private RestApiFactory() {
  }

  /**
   * Creates a rest api based on the provided {@link T} rest interface definition. {@link
   * GsonConverterFactory} used as default
   *
   * @param restInterface {@link T} with the rest interface definition
   * @param baseUrl {@link String} with the base URL
   * @param interceptors {@link List} of {@link Interceptor} to add to the rest api
   * @param gson {@link Gson} to use for serialising. Null to use default one.
   * @param logs {@link Boolean} indicating if logs are required
   * @param <T> {@link Class} with the definition of the rest interface
   * @return {@link T} with the implementation of the Rest interface
   */
  public static <T> T createApi(Class<T> restInterface,
      @NonNull String baseUrl,
      @Nullable List<Interceptor> interceptors,
      @Nullable Gson gson,
      boolean logs) {

    Timber.d("getRestApi: %s interface: %s gson: %s", baseUrl, restInterface.getName(), gson);

    return setupRetrofit(
        baseUrl,
        interceptors,
        null,
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()),
        gson != null ? gson : new GsonBuilder().create(),
        logs).create(restInterface);
  }

  /**
   * Creates a simple rest api based on the provided {@link T} rest interface definition.
   *
   * @param restInterface {@link T} with the rest interface definition
   * @param baseUrl {@link String} with the base URL
   * @param logs {@link Boolean} indicating if logs are required
   * @param <T> {@link Class} with the definition of the rest interface
   * @return {@link T} with the implementation of the Rest interface
   */
  public static <T> T createApi(Class<T> restInterface,
      @NonNull String baseUrl,
      boolean logs) {

    Timber.d("getRestApi: %s interface: %s", baseUrl, restInterface.getName());

    return setupRetrofit(
        baseUrl,
        null,
        null,
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()),
        new GsonBuilder().create(),
        logs).create(restInterface);
  }

  /**
   * @param restInterface {@link T} with the rest interface definition
   * @param baseUrl {@link String} with the base URL
   * @param interceptors {@link List} of {@link Interceptor} to add to the rest api
   * @param networkInterceptors {@link List} of {@link Interceptor} as NetworkInterceptor
   * @param gson {@link Gson} to use for serialising. Null to use default one.
   * @param logs {@link Boolean} indicating if logs are required
   * @param <T> {@link Class} with the definition of the rest interface
   * @return {@link T} with the implementation of the Rest interface
   */
  public static <T> T createApi(Class<T> restInterface,
      @NonNull String baseUrl,
      @Nullable List<Interceptor> interceptors,
      @Nullable List<Interceptor> networkInterceptors,
      @Nullable Gson gson,
      boolean logs) {

    return setupRetrofit(
        baseUrl,
        interceptors,
        networkInterceptors,
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()),
        gson != null ? gson : new GsonBuilder().create(),
        logs).create(restInterface);
  }

  /**
   * Creates a {@link Retrofit} object with the given settings
   *
   * @param url {@link String} with the Base Url
   * @param interceptors {@link List} of {@link Interceptor} to add to the rest api
   * @param networkInterceptors {@link List} of {@link Interceptor} as NetworkInterceptor
   * @param callAdapterFactory {@link retrofit2.CallAdapter.Factory}
   * @param gsonConverterFactory {@link Gson} converter to use for serialising
   * @param logs {@link Boolean} indicating if logs are required
   * @return {@link Retrofit} object with the given settings
   */
  private static Retrofit setupRetrofit(@NonNull String url,
      @Nullable List<Interceptor> interceptors,
      @Nullable List<Interceptor> networkInterceptors,
      @NonNull CallAdapter.Factory callAdapterFactory,
      @NonNull Gson gsonConverterFactory,
      boolean logs) {

    Preconditions.checkArgument(ValidationUtils.isValidURL(url), "Base URL is invalid");
    Timber.d("setupRetrofit: %s", url);

    // Add the interceptors if they exist
    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

    // Add a Log interceptor if debug mode
    List<Interceptor> interceptorList = new ArrayList<>();
    if (logs) {
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      interceptorList.add(loggingInterceptor);
    }
    // Add param interceptors
    if (interceptors != null && !interceptors.isEmpty()) {
      interceptorList.addAll(interceptors);
    }
    // Add all of them to the okHttpBuilder
    for (int i = 0; i < interceptorList.size(); i++) {
      okHttpClientBuilder.addInterceptor(interceptorList.get(i));
    }

    // Add interceptors as network interceptors
    if (networkInterceptors != null && !networkInterceptors.isEmpty()) {
      for (int i = 0; i < networkInterceptors.size(); i++) {
        okHttpClientBuilder.addNetworkInterceptor(networkInterceptors.get(i));
      }
    }

    // Finally create the client
    return new Retrofit.Builder().client(okHttpClientBuilder.build())
        .baseUrl(url.endsWith("/") ? url : url + "/")
        .addConverterFactory(GsonConverterFactory.create(gsonConverterFactory))
        .addCallAdapterFactory(callAdapterFactory)
        .build();
  }

}
