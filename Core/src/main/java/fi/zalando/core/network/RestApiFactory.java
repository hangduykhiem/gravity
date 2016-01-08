package fi.zalando.core.network;

import com.google.gson.Gson;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Patterns;

import fi.zalando.core.utils.Preconditions;
import retrofit.CallAdapter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
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
     * Creates a rest api base on the provided {@link T} restInterface definition. {@link
     * GsonConverterFactory} and {@link RxJavaCallAdapterFactory} used as default
     *
     * @param restInterface {@link T} with the rest interface definition
     * @param baseUrl       {@link String} with the base URL
     * @param gson          {@link Gson} to use for serialising. Null to use default one.
     * @param <T>           {@link Class} with the definition of the rest interface
     * @return {@link T} with the implementation of the Rest interface
     */
    public static <T> T createApi(Class<T> restInterface, @NonNull String baseUrl, @Nullable Gson
            gson) {

        Timber.d("getRestApi: " + baseUrl + " interface: " + restInterface.getName() + " " +
                "gson: " + gson);

        return setupRetrofit(baseUrl, RxJavaCallAdapterFactory.create(), gson != null ? gson :
                new Gson()).create(restInterface);
    }

    /**
     * Creates a {@link Retrofit} object with the given settings
     *
     * @param url                  {@link String} with the Base Url
     * @param callAdapterFactory   {@link retrofit.CallAdapter.Factory}
     * @param gsonConverterFactory {@link Gson} converter to use for serialising
     * @return {@link Retrofit} object with the given settings
     */
    private static Retrofit setupRetrofit(@NonNull String url, @NonNull CallAdapter.Factory
            callAdapterFactory, @NonNull Gson gsonConverterFactory) {

        Preconditions.checkArgument(Patterns.WEB_URL.matcher(url).matches(), "Base URL is invalid");
        Timber.d("setupRetrofit: " + url);

        return new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory
                .create(gsonConverterFactory)).addCallAdapterFactory(callAdapterFactory).build();
    }

}
