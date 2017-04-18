package org.zalando.core.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

/**
 * Utility class to provide Device related information
 */
public class DeviceUtils {

  @Nullable
  private static Point deviceResolution;
  @Nullable
  private static Point appUsableScreenSize;

  /**
   * Private constructor to avoid class instances
   */
  private DeviceUtils() {
  }

  /**
   * Get ISO 3166-1 alpha-2 country code for this device (or empty String if not available)
   *
   * @param context {@link TelephonyManager} reference to get the TelephonyManager instance from
   * @return {@link String) with country ISO code or empty if not available
   */
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
    return "";
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
   *
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
   * Returns the height of the status bar in pixels
   *
   * @return height of the status bar in pixels
   */
  public static int getStatusBarHeight() {
    int result = 0;
    final int resourceId = Resources.getSystem()
        .getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = Resources.getSystem().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  /**
   * Return the height of the action bar in pixels. If attribute not found, return standard value
   * of 56dp in pixels
   *
   * @param context {@link Context}
   * @return height of status bar in pixels
   */
  public static float getActionBarHeight(Context context) {
    TypedValue tv = new TypedValue();
    if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      return TypedValue.complexToDimensionPixelSize(tv.data,
          Resources.getSystem().getDisplayMetrics());
    } else {
      return dpToPx(56);
    }
  }

  /**
   * Returns the usable area of the screen, i.e. screen size minus the decorations & bottom bar.
   *
   * @param context {@link Context}
   * @return usable dimensions in {@link Point}
   */
  public static Point getAppUsableScreenSize(Context context) {

    if (appUsableScreenSize == null) {
      WindowManager windowManager = (WindowManager) context.getSystemService(Context
          .WINDOW_SERVICE);
      Display display = windowManager.getDefaultDisplay();
      appUsableScreenSize = new Point();
      display.getSize(appUsableScreenSize);
      return appUsableScreenSize;
    }
    return appUsableScreenSize;
  }

  /**
   * Gets the device's native screen resolution rotated based on the device's current screen
   * orientation.
   *
   * @param context {@link Context} of the app
   * @return {@link Point} with the device screen native resolution
   */
  public static Point getScreenResolution(Context context) {

    if (deviceResolution == null) {
      WindowManager windowManager = (WindowManager) context.getSystemService(Context
          .WINDOW_SERVICE);
      Display display = windowManager.getDefaultDisplay();
      deviceResolution = new Point();

      if (Build.VERSION.SDK_INT >= 17) {
        display.getRealSize(deviceResolution);
      } else if (Build.VERSION.SDK_INT >= 14) {
        try {
          deviceResolution.x =
              (Integer) Display.class.getMethod("getRawWidth").invoke(display);
          deviceResolution.y =
              (Integer) Display.class.getMethod("getRawHeight").invoke(display);
        } catch (Exception e) { /*nada*/ }
      }
    }
    return deviceResolution;
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
   * @param px Pixels
   * @return Converted amount in dp
   */
  public static float pxToDp(final float px) {
    return px / Resources.getSystem().getDisplayMetrics().density;
  }

  /**
   * Convert the given dp amount to pixels.
   *
   * @param dp Dps
   * @return Converted amount in pixels
   */
  public static float dpToPx(final float dp) {
    return dp * Resources.getSystem().getDisplayMetrics().density;
  }

  /**
   * Convert the given pixels amount to sp.
   *
   * @param px Pixels
   * @return Converted amount in sp
   */
  public static float pxToSp(final float px) {
    return px / Resources.getSystem().getDisplayMetrics().scaledDensity;
  }

  /**
   * Convert the given sp amount to pixels.
   *
   * @param sp Sps
   * @return Converted amount in pixels
   */
  public static float spToPx(final float sp) {
    return sp * Resources.getSystem().getDisplayMetrics().scaledDensity;
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
  private static String getDeviceIdPseudo(Context context) {
    String result = Build.SERIAL;
    result += "::" + (Build.PRODUCT.length() % 10) + (Build.BOARD.length() % 10) + (
        Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (
        Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (
        Build.MODEL.length() % 10);
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
   * @param context Application context
   * @param vibrationTime {@link Long} with the time to vibrate
   * @param timeUnit {@link TimeUnit} of the given vibration time
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
   * @param repeat the index into pattern at which to repeat, or -1 if you don't want to repeat.
   */
  public static void vibrate(final Context context, long[] pattern, int repeat) {

    // Get instance of Vibrator from current Context and apply the pattern
    ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, repeat);
  }

}
