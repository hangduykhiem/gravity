package fi.zalando.core.utils;

import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
            final TelephonyManager tm = getTelephoneManager(context);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.getDefault());
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not
                // 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country
                    // code is available
                    return networkCountry.toLowerCase(Locale.getDefault());
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
     * Returns the dimensions of the bottom navigation bar, if one is present
     * @param context {@link Context}
     * @return bottom bar dimensions in {@link Point}.
     */
    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getScreenResolution(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    /**
     * Returns the usable area of the screen, i.e. screen size minus the decorations & bottom bar.
     * @param context {@link Context}
     * @return usable dimensions in {@link Point}
     */
    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Gets the device's native screen resolution rotated based on the device's current screen
     * orientation.
     *
     * @param context {@link Context} of the app
     * @return {@link Point} with the device screen native resolution
     */
    public static Point getScreenResolution(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {/*nada*/}
        }

        return size;
    }

    /**
     * Returns the aspect ratio (screen width / screen height)
     *
     * @param context {@link Context}
     * @return aspect ratio
     */
    public static float getAspectRatio(Context context) {
        Point p = getScreenResolution(context);
        return (float) p.x / (float) p.y;
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

    /**
     * Convert the given pixels amount to sp.
     *
     * @param context Context
     * @param px      Pixels
     * @return Converted amount in sp
     */
    public static float pxToSp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * Convert the given sp amount to pixels.
     *
     * @param context Context
     * @param sp      Sps
     * @return Converted amount in pixels
     */
    public static float spToPx(final Context context, final float sp) {
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * Returns android ID. Available after FROYO.
     *
     * @param context Application Context
     * @return Android Id string
     */
    private static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Provides devices device id, first using device IMEI, second Android ID and third device board
     * ID. The order is sequential, if one success next one will be tried.
     *
     * @return Device id {@link String}
     */
    @Nullable
    public static String getDeviceId(Context context) {
        try {
            String deviceId = getTelephoneManager(context).getDeviceId();
            if (!TextUtils.isEmpty(deviceId) && TextUtils.isEmpty(deviceId.replace("0", ""))) {
                return SecurityUtils.MD5.createDigest(deviceId);
            }
            deviceId = getAndroidId(context);
            if (!TextUtils.isEmpty(deviceId) && !deviceId.equals("9774d56d682e549c")) {
                return SecurityUtils.MD5.createDigest(deviceId);
            }
            return SecurityUtils.MD5.createDigest(getDeviceIdPseudo(context));
        } catch (Exception ex) {
            return SecurityUtils.MD5.createDigest(Settings.Secure.getString(context
                    .getContentResolver(), Settings.Secure.ANDROID_ID));
        }
    }

    /**
     * Returns device's pseudo ID using board flags
     *
     * @param context Application Context
     * @return Device Board Pseudo ID
     */
    @SuppressWarnings("deprecation")
    private static String getDeviceIdPseudo(Context context) {
        String result = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            result += Build.SERIAL;
            result += "::" + (Build.PRODUCT.length() % 10) + (Build.BOARD.length() % 10) + (
                    Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (
                    Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (
                    Build.MODEL.length() % 10);
        }
        return result;
    }

    /**
     * Get the telephony manager with the context
     *
     * @param context context
     * @return telephonyManager
     */
    private static TelephonyManager getTelephoneManager(Context context) {

        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Performs vibration of the time specified in the parameters
     *
     * @param context       Application context
     * @param vibrationTime {@link Long} with the time to vibrate
     * @param timeUnit      {@link TimeUnit} of the given vibration time
     */
    public static void vibrate(final Context context,
                               long vibrationTime,
                               TimeUnit timeUnit) {

        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
                .vibrate(timeUnit.toMillis(vibrationTime));
    }

    /**
     * Vibrate with a given pattern.
     *
     * <p> Pass in an array of ints that are the durations for which to turn on or off the vibrator
     * in milliseconds.  The first value indicates the number of milliseconds to wait before turning
     * the vibrator on.  The next value indicates the number of milliseconds for which to keep the
     * vibrator on before turning it off.  Subsequent values alternate between durations in
     * milliseconds to turn the vibrator off or to turn the vibrator on. </p><p> To cause the
     * pattern to repeat, pass the index into the pattern array at which to start the repeat, or -1
     * to disable repeating. </p> <p>This method requires the caller to hold the permission {@link
     * android.Manifest.permission#VIBRATE}.
     *
     * @param pattern an array of longs of times for which to turn the vibrator on or off.
     * @param repeat  the index into pattern at which to repeat, or -1 if you don't want to repeat.
     */
    public static void vibrate(final Context context, long[] pattern, int repeat) {

        // Get instance of Vibrator from current Context and apply the pattern
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, repeat);
    }

}
