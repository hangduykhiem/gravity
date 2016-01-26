package fi.zalando.core.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.view.WindowManager;

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

    /**
     * Gets the device's native screen resolution rotated based on the device's current screen
     * orientation.
     *
     * @param context {@link Context} of the app
     * @return {@link Point} with the device screen native resolution
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Point screenResolution(Context context) {

        // TargetApi is needed To remove warning about using getRealSize prior to API 17

        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        Point screenResolution = new Point();
        windowManager.getDefaultDisplay().getRealSize(screenResolution);
        return screenResolution;
    }

    /**
     * Converts the given pixel amount to dp.
     * @param context Context
     * @param px Pixels
     * @return Converted amount in dp
     */
    public static float pxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * Convert the given dp amount to pixels.
     * @param context Context
     * @param dp Dps
     * @return Converted amount in pixels
     */
    public static float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
