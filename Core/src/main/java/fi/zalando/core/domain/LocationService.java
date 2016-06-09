package fi.zalando.core.domain;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import fi.zalando.core.data.LocationRepository;
import fi.zalando.core.helper.CleaningHelper;
import rx.Observable;

/**
 * Service to request Location updates from device
 *
 * Created by jduran on 27/01/16.
 */
@Singleton
public class LocationService extends BaseService {

    private final LocationRepository locationRepository;

    /**
     * Constructor
     *
     * @param locationRepository {@link LocationRepository} dependency
     * @param cleaningHelper     {@link CleaningHelper} dependency
     */
    @Inject
    public LocationService(LocationRepository locationRepository, CleaningHelper cleaningHelper) {

        super(cleaningHelper);
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
     * @param accuracyCriteria        {@link Integer} with the accuracy criteria. @See {@link
     *                                LocationRequest}
     * @return {@link Observable} that provides {@link LatLng} updates
     */
    public Observable<LatLng> loadLocations(Long locationUpdateFrequency, TimeUnit timeUnit, int
            accuracyCriteria) {

        return locationRepository.loadLocations(timeUnit.toMillis(locationUpdateFrequency),
                accuracyCriteria).compose(applySchedulers());
    }
}
