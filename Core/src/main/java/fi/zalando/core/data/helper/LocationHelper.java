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

    /**
     * Constructor
     *
     * @param applicationContext {@link Context} of the application
     * @param locationManager    {@link LocationManager} from the device
     */
    public LocationHelper(Context applicationContext, LocationManager locationManager) {

        this.applicationContext = applicationContext.getApplicationContext();
        this.locationManager = locationManager;
    }

    public Observable<LatLng> loadCurrentLocation() {

        return Observable.create(new Observable.OnSubscribe<LatLng>() {
            @Override
            public void call(Subscriber<? super LatLng> subscriber) {

                fillSubscriberWithLocation(subscriber);
            }
        });
    }

    /**
     * Fills the subscriber with the location fetching logic
     *
     * @param subscriber {@link Subscriber} to fill
     * @throws SecurityException {@link SecurityException} if location is not granted
     */
    private void fillSubscriberWithLocation(Subscriber<? super LatLng> subscriber) throws
            SecurityException {

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
                subscriber.onCompleted();
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

        // Init location criteria
        final Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);

        // listen for updates
        locationManager.requestSingleUpdate(locationCriteria, locationListener, null);

        // Unregister listener if unsubscribed
        subscriber.add(Subscriptions.create(() -> locationManager.removeUpdates
                (locationListener)));
    }
}
