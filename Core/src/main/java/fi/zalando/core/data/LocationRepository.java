package fi.zalando.core.data;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import fi.zalando.core.data.helper.LocationHelper;
import fi.zalando.core.data.entity.transformer.LocationDataTransformerFactory;
import fi.zalando.core.utils.Preconditions;
import rx.Observable;

/**
 * Repository that provides Location related data loading functionalities
 *
 * Created by jduran on 19/01/16.
 */
public class LocationRepository extends BaseRepository {

    private final ReactiveLocationProvider reactiveLocationProvider;
    private final LocationHelper locationHelper;

    /**
     * Constructor
     *
     * @param reactiveLocationProvider {@link ReactiveLocationProvider} instance
     * @param locationHelper           {@link LocationHelper} to use as backup if G.P.Services
     *                                 fails
     */
    public LocationRepository(ReactiveLocationProvider reactiveLocationProvider, LocationHelper
            locationHelper) {

        this.reactiveLocationProvider = reactiveLocationProvider;
        this.locationHelper = locationHelper;
    }

    /**
     * Provides an {@link Observable} that provides asynchronously current {@link LatLng} using the
     * best available criteria.
     *
     * @return {@link Observable} to load current {@link LatLng}
     */
    public Observable<LatLng> loadCurrentLocation() {

        return reactiveLocationProvider.getLastKnownLocation().map
                (LocationDataTransformerFactory.getLocationDataTransformer()).onErrorResumeNext
                (throwable -> locationHelper.loadCurrentLocation());
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

        // Build location request adding user defined settings
        LocationRequest locationRequest = LocationRequest.create().setPriority(accuracyCriteria)
                .setInterval(locationUpdateFrequency < 0L ? 0L : locationUpdateFrequency);

        return reactiveLocationProvider.getUpdatedLocation(locationRequest).map
                (LocationDataTransformerFactory.getLocationDataTransformer()).onErrorResumeNext
                (throwable -> locationHelper.loadLocations(locationUpdateFrequency,
                        accuracyCriteria));
    }
}
