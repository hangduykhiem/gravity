package fi.zalando.core.data.helper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.os.Bundle;
import android.support.annotation.NonNull;

import fi.zalando.core.data.helper.throwable.GooglePlayServicesConnectionResultThrowable;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Helper class that provides methods to deal with Google Play Services
 *
 * Created by jduran on 18/01/16.
 */
public class GooglePlayServicesHelper {

    public Observable<GoogleApiClient> loadGooglePlayServices(GoogleApiClient googleApiClient) {

        return Observable.create(new ObservableConnection(googleApiClient));
    }

    private final class ObservableConnection implements Observable.OnSubscribe<GoogleApiClient>,
            GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

        private final GoogleApiClient googleAPIClient;
        private Subscriber<? super GoogleApiClient> observer;

        /**
         * Private constructor to avoid external instances from this class
         *
         * @param client {@link GoogleApiClient} to connect
         */
        private ObservableConnection(GoogleApiClient client) {

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
            observer.onError(new GooglePlayServicesConnectionResultThrowable
                    (suspendedConnectionResult.toString(), suspendedConnectionResult));
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            observer.onError(new GooglePlayServicesConnectionResultThrowable(connectionResult
                    .toString(), connectionResult));
        }

        @Override
        public void call(Subscriber<? super GoogleApiClient> subscriber) {

            observer = subscriber;
            googleAPIClient.registerConnectionCallbacks(this);
            googleAPIClient.registerConnectionFailedListener(this);
            googleAPIClient.connect();

            observer.add(Subscriptions.create(() -> {

                if (googleAPIClient.isConnected() || googleAPIClient.isConnecting()) {
                    googleAPIClient.disconnect();
                }
                googleAPIClient.unregisterConnectionFailedListener(ObservableConnection.this);
                googleAPIClient.unregisterConnectionCallbacks(ObservableConnection.this);
            }));
        }
    }
}
