package fi.zalando.core.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import java.util.Locale;

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
        }
        catch (Exception ex) {
            return null;
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
}
