package fi.zalando.core.domain;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

import java.util.concurrent.TimeUnit;

import fi.zalando.core.data.LocationRepository;
import rx.Observable;

/**
 * Service to request Location updates from device
 *
 * Created by jduran on 27/01/16.
 */
public class LocationService extends BaseService {

    private final LocationRepository locationRepository;

    /**
     * Constructor
     *
     * @param locationRepository initialize the {@link LocationRepository}
     */
    public LocationService(LocationRepository locationRepository) {

        this.locationRepository = locationRepository;
    }

    /**
     * Provides an {@link Observable} that provides asynchronously current {@link LatLng}
     *
     * @return {@link Observable} to load current {@link LatLng}
     */
    public Observable<LatLng> loadCurrentLocation() {

        return locationRepository.loadCurrentLocation().compose(applySchedulers());
    }

    /**
     * Provides an {@link Observable} that calls onNext everytime there is a {@link Location}
     * update
     *
     * @param locationUpdateFrequency {@link Long} with the milliseconds about the frequency of the
     *                                required updates
     * @param timeUnit                {@link TimeUnit} if the provided frequency
     * @return {@link Observable} that provides {@link LatLng} updates
     */
    public Observable<LatLng> loadLocations(Long locationUpdateFrequency, TimeUnit timeUnit) {

        return locationRepository.loadLocations(timeUnit.toMillis(locationUpdateFrequency))
                .compose(applySchedulers());
    }
}
