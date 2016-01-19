package fi.zalando.core.data;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.content.Context;
import android.location.Location;

import fi.zalando.core.data.helper.GooglePlayServicesHelper;
import fi.zalando.core.data.helper.LocationHelper;
import fi.zalando.core.exception.PermissionSecurityException;
import fi.zalando.core.utils.PermissionUtils;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Repository that provides Location related data loading functionalities
 *
 * Created by jduran on 19/01/16.
 */
public class LocationRepository {

    private final Context applicationContext;
    private final GooglePlayServicesHelper googlePlayServicesHelper;
    private final LocationHelper locationHelper;

    /**
     * Constructor
     *
     * @param applicationContext       {@link Context} of the application
     * @param googlePlayServicesHelper {@link GooglePlayServicesHelper} to connect G.P.Services
     * @param locationHelper           {@link LocationHelper} to use as backup if G.P.Services
     *                                 fails
     */
    public LocationRepository(Context applicationContext, GooglePlayServicesHelper
            googlePlayServicesHelper, LocationHelper locationHelper) {

        this.applicationContext = applicationContext;
        this.googlePlayServicesHelper = googlePlayServicesHelper;
        this.locationHelper = locationHelper;
    }

    /**
     * Provides an {@link Observable} that provides asynchronously current {@link LatLng}
     *
     * @return {@link Observable} to load current {@link LatLng}
     */
    public Observable<LatLng> loadCurrentLocation() {

        // First, try fetching location using google play services client
        return googlePlayServicesHelper.loadGooglePlayServices(new GoogleApiClient.Builder
                (applicationContext).addApi(LocationServices.API).build()).
                // Once Google Api Client connected, use it to fetch the location
                        flatMap(googleApiClient -> Observable.create(new Observable
                        .OnSubscribe<LatLng>() {
                    @Override
                    public void call(Subscriber<? super LatLng> subscriber) {

                        fillSubscriberWithLocation(googleApiClient, subscriber);
                    }
                    // If fails, use default location manager
                })).onErrorResumeNext(throwable -> {
                    Timber.w(throwable, "Google Play services location fetch failed");
                    return locationHelper.loadCurrentLocation();
                });
    }

    /**
     * Fills the subscriber with the location fetching logic
     *
     * @param googleApiClient {@link GoogleApiClient} to use for fetching location
     * @param subscriber      {@link Subscriber} to fill
     * @throws SecurityException {@link SecurityException} if location is not granted
     */
    private void fillSubscriberWithLocation(GoogleApiClient googleApiClient, Subscriber<? super
            LatLng> subscriber) throws SecurityException {

        // Throw error if access location is not granted
        if (!PermissionUtils.checkRuntimePermissions(applicationContext, Manifest.permission
                .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            subscriber.onError(new PermissionSecurityException(Manifest.permission
                    .ACCESS_FINE_LOCATION));
            return;
        }
        Location lastKnwonLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        // If last known location available, use it!
        if (lastKnwonLocation != null) {
            subscriber.onNext(new LatLng(lastKnwonLocation.getLatitude(),
                    lastKnwonLocation.getLongitude()));
            subscriber.onCompleted();
        } else {
            // If last known location not available, look for updates
            // Create location listener to handle the callback
            LocationListener locationListener = location -> {
                subscriber.onNext(new LatLng(location.getLatitude(), location
                        .getLongitude()));
                subscriber.onCompleted();
            };
            // Create LocationRequest
            LocationRequest locationRequest = new LocationRequest();
            // Get the update ASAP
            locationRequest.setInterval(0);
            // Get the update ASAP
            locationRequest.setFastestInterval(0);
            locationRequest.setPriority(LocationRequest
                    .PRIORITY_BALANCED_POWER_ACCURACY);
            // Start listening for updates
            LocationServices.FusedLocationApi.requestLocationUpdates
                    (googleApiClient, locationRequest, locationListener);
            // If observable is unsubscribed, stop listening for updates
            subscriber.add(Subscriptions.create(() ->
                    LocationServices.FusedLocationApi.removeLocationUpdates
                            (googleApiClient, locationListener)));
        }
    }

}
