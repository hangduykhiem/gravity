package fi.zalando.core.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Patterns;

import java.util.List;

import fi.zalando.core.utils.Preconditions;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import timber.log.Timber;

/**
 * Factory class that helps building Rest Api interfaces using RetroFit.
 *
 * Created by jduran on 07/12/15.
 */
public final class RestApiFactory {

    /**
     * Private constructor to force static access to methods
     */
    private RestApiFactory() {
    }

    /**
     * Creates a rest api based on the provided {@link T} rest interface definition. {@link
     * GsonConverterFactory} and {@link RxJavaCallAdapterFactory} used as default
     *
     * @param restInterface {@link T} with the rest interface definition
     * @param baseUrl       {@link String} with the base URL
     * @param interceptors  {@link List} of {@link Interceptor} to add to the rest api
     * @param gson          {@link Gson} to use for serialising. Null to use default one.
     * @param <T>           {@link Class} with the definition of the rest interface
     * @return {@link T} with the implementation of the Rest interface
     */
    public static <T> T createApi(Class<T> restInterface, @NonNull String baseUrl, @Nullable
    List<Interceptor> interceptors, @NonNull Gson gson) {

        Timber.d("getRestApi: " + baseUrl + " interface: " + restInterface.getName() + " " +
                "gson: " + gson);

        return setupRetrofit(baseUrl, interceptors, RxJavaCallAdapterFactory.create(), gson).create
                (restInterface);
    }

    /**
     * Creates a simple rest api based on the provided {@link T} rest interface definition.
     *
     * @param restInterface {@link T} with the rest interface definition
     * @param baseUrl       {@link String} with the base URL
     * @param <T>           {@link Class} with the definition of the rest interface
     * @return {@link T} with the implementation of the Rest interface
     */
    public static <T> T createApi(Class<T> restInterface, @NonNull String baseUrl) {

        Timber.d("getRestApi: " + baseUrl + " interface: " + restInterface.getName());

        return setupRetrofit(baseUrl, null, RxJavaCallAdapterFactory.create(), new GsonBuilder()
                .create()).create(restInterface);
    }

    /**
     * Creates a {@link Retrofit} object with the given settings
     *
     * @param url                  {@link String} with the Base Url
     * @param callAdapterFactory   {@link retrofit2.CallAdapter.Factory}
     * @param gsonConverterFactory {@link Gson} converter to use for serialising
     * @return {@link Retrofit} object with the given settings
     */
    private static Retrofit setupRetrofit(@NonNull String url, @Nullable List<Interceptor>
            interceptors, @NonNull CallAdapter.Factory callAdapterFactory, @NonNull Gson
                                                  gsonConverterFactory) {

        Preconditions.checkArgument(Patterns.WEB_URL.matcher(url).matches(), "Base URL is invalid");
        Timber.d("setupRetrofit: " + url);

        // Add the interceptors if they exist
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if (interceptors != null && !interceptors.isEmpty()) {
            for (int i = 0; i < interceptors.size(); i++) {
                okHttpClientBuilder.addInterceptor(interceptors.get(i));
            }
        }

        // Finally create the client
        return new Retrofit.Builder().client(okHttpClientBuilder.build()).baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gsonConverterFactory))
                .addCallAdapterFactory(callAdapterFactory).build();
    }

}
