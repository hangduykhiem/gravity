package fi.zalando.core.data.helper;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import javax.inject.Inject;

import rx.Observable;

/**
 * Helper class that provides methods to deal with Google Play Services
 *
 * Created by jduran on 18/01/16.
 */
public class GooglePlayServicesHelper {

    private final ReactiveLocationProvider reactiveLocationProvider;

    /**
     * Constructor
     *
     * @param reactiveLocationProvider {@link ReactiveLocationProvider} instance
     */
    @Inject
    public GooglePlayServicesHelper(ReactiveLocationProvider reactiveLocationProvider) {

        this.reactiveLocationProvider = reactiveLocationProvider;
    }

    /**
     * {@link Observable} to connect asynchronously google play services
     *
     * @param apis {@link Api}s to connect to
     * @return {@link Observable} with the {@link GoogleApiClient} connected
     */
    public Observable<GoogleApiClient> loadGooglePlayServices(Api... apis) {

        return reactiveLocationProvider.getGoogleApiClientObservable(apis);
    }
}
