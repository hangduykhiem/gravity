package fi.zalando.core.utils;

import android.location.LocationManager;

/**
 * Utility class to provide Device related information
 *
 * Created by jduran on 21/01/16.
 */
public class DeviceUtils {

    /**
     * Private constructor to avoid class instances
     */
    private DeviceUtils() {
    }

    /**
     * Checks if GPS/Network location providers are enabled on the device
     *
     * @param locationManager {@link LocationManager} of the device
     * @return {@link Boolean} indicating if the location services are enabled
     */
    public static boolean isLocationEnabled(LocationManager locationManager) {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
