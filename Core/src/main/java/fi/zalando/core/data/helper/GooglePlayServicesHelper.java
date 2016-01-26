package fi.zalando.core.data.helper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import fi.zalando.core.data.helper.exception.GooglePlayServicesConnectionResultException;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Helper class that provides methods to deal with Google Play Services
 *
 * Created by jduran on 18/01/16.
 */
public class GooglePlayServicesHelper {

    /**
     * Builds a {@link GoogleApiClient} that will make use of {@link LocationServices} API
     *
     * @param applicationContext {@link Context} of the application
     * @return {@link GoogleApiClient} to use location services
     */
    public GoogleApiClient createLocationApiGoogleApiClient(Context applicationContext) {

        return new GoogleApiClient.Builder(applicationContext.getApplicationContext()).addApi
                (LocationServices.API).build();
    }

    /**
     * Provides an {@link Observable} that connects the given {@link GoogleApiClient}
     * asynchronously
     *
     * @param googleApiClient {@link GoogleApiClient} to connect
     * @return {@link Observable} to connect asynchronously the {@link GoogleApiClient}
     */
    public Observable<GoogleApiClient> loadGooglePlayServices(GoogleApiClient googleApiClient) {

        return Observable.create(new ConnectionObservable(googleApiClient));
    }

    /**
     * {@link Observable} that provides a connected {@link GoogleApiClient} asynchronously
     */
    @VisibleForTesting
    private final class ConnectionObservable implements Observable.OnSubscribe<GoogleApiClient>,
            GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

        private final GoogleApiClient googleAPIClient;
        private Subscriber<? super GoogleApiClient> observer;

        /**
         * Private constructor to avoid external instances from this class
         *
         * @param client {@link GoogleApiClient} to connect
         */
        private ConnectionObservable(GoogleApiClient client) {

            this.googleAPIClient = client;
        }

        @Override
        public void onConnected(Bundle bundle) {

            observer.onNext(googleAPIClient);
        }

        @Override
        public void onConnectionSuspended(int i) {

            ConnectionResult suspendedConnectionResult = new ConnectionResult(ConnectionResult
                    .CANCELED);
            observer.onError(new GooglePlayServicesConnectionResultException
                    (suspendedConnectionResult.toString(), suspendedConnectionResult));
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            observer.onError(new GooglePlayServicesConnectionResultException(connectionResult
                    .toString(), connectionResult));
        }

        @Override
        public void call(Subscriber<? super GoogleApiClient> subscriber) {

            observer = subscriber;
            googleAPIClient.registerConnectionCallbacks(this);
            googleAPIClient.registerConnectionFailedListener(this);
            googleAPIClient.connect();

            // Add an event to be called when unsubscribe the observable
            observer.add(Subscriptions.create(() -> {

                if (googleAPIClient.isConnected() || googleAPIClient.isConnecting()) {
                    googleAPIClient.disconnect();
                }
                googleAPIClient.unregisterConnectionFailedListener(ConnectionObservable.this);
                googleAPIClient.unregisterConnectionCallbacks(ConnectionObservable.this);
            }));
        }
    }
}
