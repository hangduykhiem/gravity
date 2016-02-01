package fi.zalando.core.data.helper;

import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import fi.zalando.core.exception.PermissionSecurityException;
import fi.zalando.core.exception.ServiceDisabledException;
import fi.zalando.core.utils.DeviceUtils;
import fi.zalando.core.utils.PermissionUtils;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class LocationHelper {

    private final Context applicationContext;
    private final LocationManager locationManager;
    private final Criteria criteria;

    /**
     * Constructor
     *
     * @param applicationContext {@link Context} of the application
     * @param locationManager    {@link LocationManager} from the device
     */
    public LocationHelper(Context applicationContext, LocationManager locationManager) {

        this.applicationContext = applicationContext.getApplicationContext();
        this.locationManager = locationManager;
        this.criteria = initDefaultCriteria();
    }

    /**
     * Constructor
     *
     * @param applicationContext {@link Context} of the application
     * @param locationManager    {@link LocationManager} from the device
     * @param criteriaToUse      {@link Criteria} to use when fetching the location
     */
    public LocationHelper(Context applicationContext, LocationManager locationManager, Criteria
            criteriaToUse) {

        this.applicationContext = applicationContext.getApplicationContext();
        this.locationManager = locationManager;
        this.criteria = criteriaToUse != null ? criteriaToUse : initDefaultCriteria();
    }

    /**
     * Initialises a default {@link Criteria} to fetch current {@link Location}
     *
     * @return {@link Criteria} with default setup
     */
    private Criteria initDefaultCriteria() {

        final Criteria defaultLocationCriteria = new Criteria();
        defaultLocationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        defaultLocationCriteria.setPowerRequirement(Criteria.POWER_HIGH);
        return defaultLocationCriteria;
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
     * Provides an {@link Observable} that provides asynchronously current {@link LatLng}
     *
     * @return {@link Observable} to load current {@link LatLng}
     */
    public Observable<LatLng> loadCurrentLocation() {

        return Observable.create(new Observable.OnSubscribe<LatLng>() {
            @Override
            public void call(Subscriber<? super LatLng> subscriber) {

                fillSubscriberWithLocation(subscriber, true, 0L);
            }
        });
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

        return Observable.create(new Observable.OnSubscribe<LatLng>() {
            @Override
            public void call(Subscriber<? super LatLng> subscriber) {

                fillSubscriberWithLocation(subscriber, singleLocationUpdateRequest,
                        locationUpdateFrequency);
            }
        });
    }

    /**
     * Fills the subscriber with the location fetching logic
     *
     * @param subscriber                  {@link Subscriber} to fill
     * @param singleLocationUpdateRequest {@link Boolean} indicating if only location update is
     *                                    required
     * @param locationUpdateFrequency     {@link Long} with the amount of time location updates are
     *                                    required
     * @throws SecurityException {@link SecurityException} if location is not granted
     */
    private void fillSubscriberWithLocation(Subscriber<? super LatLng> subscriber, boolean
            singleLocationUpdateRequest, long locationUpdateFrequency) throws SecurityException {

        // Throw error if access location is not granted
        if (!PermissionUtils.checkRuntimePermissions(applicationContext, Manifest.permission
                .ACCESS_FINE_LOCATION)) {

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

        final LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                subscriber.onNext(new LatLng(location.getLatitude(), location
                        .getLongitude()));

                // If we are requesting a single location update, call on completed
                // as soon as we get one location
                if (singleLocationUpdateRequest) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // no operations
            }

            @Override
            public void onProviderEnabled(String provider) {
                // no operations
            }

            @Override
            public void onProviderDisabled(String provider) {
                // no operations
            }
        };

        // Make a single request or register for multiple changes otherwise
        if (singleLocationUpdateRequest) {
            locationManager.requestSingleUpdate(criteria, locationListener, null);
        } else {
            locationManager.requestLocationUpdates(locationUpdateFrequency, 10f, criteria,
                    locationListener, null);
        }

        // Unregister listener if unsubscribed
        subscriber.add(Subscriptions.create(() -> locationManager.removeUpdates
                (locationListener)));
    }
}
