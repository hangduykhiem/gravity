package fi.zalando.core.data.entity.transformer;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

import rx.functions.Func1;

/**
 * Factory class that creates data transformers for location data
 *
 * @author by jduran on 16/02/2016
 */
public class LocationDataTransformerFactory {

    /**
     * Private constructor to avoid object reference
     */
    private LocationDataTransformerFactory() {
    }

    /**
     * Returns a {@link Func1} to transform {@link Location} into {@link LatLng} objects
     *
     * @return {@link Func1} to transform {@link Location} into {@link LatLng}
     */
    public static Func1<Location, LatLng> getLocationDataTransformer() {

        return location -> new LatLng(location.getLatitude(), location.getLongitude());
    }

}
