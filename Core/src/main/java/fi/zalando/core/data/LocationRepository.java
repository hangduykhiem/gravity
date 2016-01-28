package fi.zalando.core.data;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import fi.zalando.core.data.helper.GooglePlayServicesHelper;
import fi.zalando.core.data.helper.LocationHelper;
import fi.zalando.core.exception.PermissionSecurityException;
import fi.zalando.core.exception.ServiceDisabledException;
import fi.zalando.core.utils.DeviceUtils;
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
public class LocationRepository extends BaseRepository {

    private final Context applicationContext;
    private final GooglePlayServicesHelper googlePlayServicesHelper;
    private final LocationManager locationManager;
    private final LocationHelper locationHelper;

    /**
     * Constructor
     *
     * @param applicationContext       {@link Context} of the application
     * @param googlePlayServicesHelper {@link GooglePlayServicesHelper} to connect G.P.Services
     * @param locationManager          {@link LocationManager} of the app
     * @param locationHelper           {@link LocationHelper} to use as backup if G.P.Services
     *                                 fails
     */
    public LocationRepository(Context applicationContext, GooglePlayServicesHelper
            googlePlayServicesHelper, LocationManager locationManager, LocationHelper
                                      locationHelper) {

        this.applicationContext = applicationContext;
        this.googlePlayServicesHelper = googlePlayServicesHelper;
        this.locationManager = locationManager;
        this.locationHelper = locationHelper;
    }

    /**
     * Provides an {@link Observable} that provides asynchronously current {@link LatLng}
     *
     * @return {@link Observable} to load current {@link LatLng}
     */
    public Observable<LatLng> loadCurrentLocation() {

        return createLocationUpdatesObservable(true, 0L);
    }

    /**
     * Provides an {@link Observable} that calls onNext everytime there is a {@link Location}
     * update
     *
     * @param locationUpdateFrequency {@link Long} with the milliseconds about the frequency of the
     *                                required updates
     * @return {@link Observable} that provides {@link LatLng} updates
     */
    public Observable<LatLng> loadLocations(long locationUpdateFrequency) {

        return createLocationUpdatesObservable(false, locationUpdateFrequency);
    }

    /**
     * Creates the {@link Observable} that will load the {@link LatLng} updates
     *
     * @param singleLocationUpdateRequest {@link Boolean} indicating if we are just requesting a
     *                                    single update
     * @param locationUpdateFrequency     {@link Long} with the milliseconds about the frequency of
     *                                    the required updates
     * @return {@link Observable} to load {@link LatLng} asynchronously
     */
    private Observable<LatLng> createLocationUpdatesObservable(boolean singleLocationUpdateRequest,
                                                               long locationUpdateFrequency) {

        // First, try fetching location using google play services client
        return googlePlayServicesHelper.loadGooglePlayServices(googlePlayServicesHelper
                .createLocationApiGoogleApiClient(applicationContext)).
                // Once Google Api Client connected, use it to fetch the location
                        flatMap(googleApiClient -> Observable.create(new Observable
                        .OnSubscribe<LatLng>() {
                    @Override
                    public void call(Subscriber<? super LatLng> subscriber) {

                        fillSubscriberWithLocation(googleApiClient, subscriber,
                                singleLocationUpdateRequest, locationUpdateFrequency);
                    }
                    // If fails, use default location manager
                })).onErrorResumeNext(throwable -> {
                    Timber.w(throwable, "Google Play services location fetch failed");

                    if (singleLocationUpdateRequest) {
                        return locationHelper.loadCurrentLocation();
                    } else {
                        return locationHelper.loadLocations(locationUpdateFrequency);
                    }
                });
    }

    /**
     * Fills the subscriber with the location fetching logic
     *
     * @param googleApiClient         {@link GoogleApiClient} to use for fetching location
     * @param subscriber              {@link Subscriber} to fill
     * @param singleLocationUpdate    {@link Boolean} indicating if only location update is
     *                                required
     * @param locationUpdateFrequency {@link Long} with the amount of time location updates are
     *                                required
     * @throws SecurityException {@link SecurityException} if location is not granted
     */
    private void fillSubscriberWithLocation(GoogleApiClient googleApiClient, Subscriber<? super
            LatLng> subscriber, boolean singleLocationUpdate, long locationUpdateFrequency)
            throws SecurityException {

        // Throw error if access location is not granted
        if (!PermissionUtils.checkRuntimePermissions(applicationContext, Manifest.permission
                .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            subscriber.onError(new PermissionSecurityException(Manifest.permission
                    .ACCESS_FINE_LOCATION));
            return;
        }

        // Throw error if location services are disabled
        if (!DeviceUtils.isLocationEnabled(locationManager)) {

            subscriber.onError(new ServiceDisabledException(ServiceDisabledException.ServiceType
                    .LOCATION_SERVICES));
            return;
        }

        Location lastKnownLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        // If last known location available, use it!
        if (lastKnownLocation != null) {

            subscriber.onNext(new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude()));

            // If we are requesting a single location update, call on completed
            // as soon as we get one location
            if (singleLocationUpdate) {
                subscriber.onCompleted();
                return;
            }
        }

        // Register for location updates if last known location was not known or location updates
        // are required until further notice

        // If last known location not available, look for updates
        // Create location listener to handle the callback
        LocationListener locationListener = location -> {
            subscriber.onNext(new LatLng(location.getLatitude(), location
                    .getLongitude()));

            // If we are requesting a single location update, call on completed
            // as soon as we get one location
            if (singleLocationUpdate) {
                subscriber.onCompleted();
            }
        };
        // Create LocationRequest
        LocationRequest locationRequest = new LocationRequest();
        // Get the update as defined by the repository user
        locationRequest.setInterval(locationUpdateFrequency);
        // Get the update as defined by the repository user
        locationRequest.setFastestInterval(locationUpdateFrequency);
        // Set high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Start listening for updates
        LocationServices.FusedLocationApi.requestLocationUpdates
                (googleApiClient, locationRequest, locationListener);
        // If observable is unsubscribed, stop listening for updates
        subscriber.add(Subscriptions.create(() -> {
            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates
                        (googleApiClient, locationListener);
            }
        }));
    }

}
