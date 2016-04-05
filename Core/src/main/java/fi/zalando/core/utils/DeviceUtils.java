package fi.zalando.core.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import timber.log.Timber;

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
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context {@link TelephonyManager} reference to get the TelephonyManager instance from
     * @return {@link String) with country ISO code or null
     */
    @Nullable
    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase();
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not
                // 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country
                    // code is available
                    return networkCountry.toLowerCase();
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Error getting user country");
        }
        return null;
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

    /**
     * Gets the device's native screen resolution rotated based on the device's current screen
     * orientation.
     *
     * @param context {@link Context} of the app
     * @return {@link Point} with the device screen native resolution
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Point getScreenResolution(Context context) {

        // TargetApi is needed To remove warning about using getRealSize prior to API 17

        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        Point screenResolution = new Point();
        windowManager.getDefaultDisplay().getRealSize(screenResolution);
        return screenResolution;
    }

    /**
     * Returns the aspect ratio (screen width / screen height)
     * @param context {@link Context}
     * @return aspect ratio
     */
    public static float getAspectRatio(Context context) {
        Point p = getScreenResolution(context);
        return (float)p.x / (float)p.y;
    }

    /**
     * Converts the given pixel amount to dp.
     *
     * @param context Context
     * @param px      Pixels
     * @return Converted amount in dp
     */
    public static float pxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * Convert the given dp amount to pixels.
     *
     * @param context Context
     * @param dp      Dps
     * @return Converted amount in pixels
     */
    public static float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
