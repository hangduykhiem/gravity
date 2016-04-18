package fi.zalando.core.data.helper;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import fi.zalando.core.data.entity.transformer.LocationDataTransformerFactory;
import fi.zalando.core.exception.PermissionSecurityException;
import fi.zalando.core.exception.ServiceDisabledException;
import fi.zalando.core.utils.DeviceUtils;
import fi.zalando.core.utils.PermissionUtils;
import fi.zalando.core.utils.Preconditions;
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
     * @param accuracyCriteria        {@link Integer} with the accuracy criteria. @See {@link
     *                                LocationRequest}
     * @return {@link Observable} that provides {@link LatLng} updates
     */
    public Observable<LatLng> loadLocations(long locationUpdateFrequency, int accuracyCriteria) {

        // Check that requested criteria is right
        Preconditions.checkArgument(accuracyCriteria >= LocationRequest.PRIORITY_HIGH_ACCURACY &&
                accuracyCriteria <= LocationRequest.PRIORITY_NO_POWER, "Accuracy criteria " +
                "parameter is invalid, please, check LocationRequest constants");

        return createLocationUpdatesObservable(false, locationUpdateFrequency, accuracyCriteria);
    }

    /**
     * Provides an {@link Observable} that provides asynchronously current {@link LatLng}
     *
     * @return {@link Observable} to load current {@link LatLng}
     */
    public Observable<LatLng> loadCurrentLocation() {

        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {

                fillSubscriberWithLocation(subscriber, true, 0L, LocationRequest
                        .PRIORITY_BALANCED_POWER_ACCURACY);
            }
        }).map(LocationDataTransformerFactory.getLocationDataTransformer());
    }

    /**
     * Creates the {@link Observable} that will load the {@link LatLng} updates
     *
     * @param singleLocationUpdateRequest {@link Boolean} indicating if we are just requesting a
     *                                    single update
     * @param locationUpdateFrequency     {@link Long} with the milliseconds about the frequency of
     *                                    the required updates
     * @param accuracyCriteria            {@link Integer} with the accuracy criteria. @See {@link
     *                                    LocationRequest}
     * @return {@link Observable} to load {@link LatLng} asynchronously
     */
    private Observable<LatLng> createLocationUpdatesObservable(boolean singleLocationUpdateRequest,
                                                               long locationUpdateFrequency, int
                                                                       accuracyCriteria) {

        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {

                fillSubscriberWithLocation(subscriber, singleLocationUpdateRequest,
                        locationUpdateFrequency, accuracyCriteria);
            }
        }).map(LocationDataTransformerFactory.getLocationDataTransformer());
    }

    /**
     * Fills the subscriber with the location fetching logic
     *
     * @param subscriber                  {@link Subscriber} to fill
     * @param singleLocationUpdateRequest {@link Boolean} indicating if only location update is
     *                                    required
     * @param locationUpdateFrequency     {@link Long} with the amount of time location updates are
     *                                    required
     * @param accuracyCriteria            {@link Integer} with the accuracy criteria. @See {@link
     *                                    LocationRequest}
     * @throws SecurityException {@link SecurityException} if location is not granted
     */
    private void fillSubscriberWithLocation(Subscriber<? super Location> subscriber, boolean
            singleLocationUpdateRequest, long locationUpdateFrequency, int accuracyCriteria)
            throws SecurityException {

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

                subscriber.onNext(location);

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

        // Change Criteria with the given one
        // Set accuracy according to the parameter
        switch (accuracyCriteria) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                break;
            case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                break;
            case LocationRequest.PRIORITY_LOW_POWER:
            case LocationRequest.PRIORITY_NO_POWER:
                criteria.setAccuracy(Criteria.ACCURACY_LOW);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                break;
        }

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
