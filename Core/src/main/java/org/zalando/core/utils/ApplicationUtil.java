package org.zalando.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import timber.log.Timber;

/**
 * Utility class to provide information about the app
 */
public class ApplicationUtil {

  /**
   * Private constructor to avoid object instance
   */
  private ApplicationUtil() {
  }

  /**
   * Provides the {@link String} with the app version
   *
   * @param context {@link Context} of the app
   * @return {@link String} with the version name
   */
  @Nullable
  public static String getAppVersion(Context context) {

    PackageInfo pInfo = null;
    try {
      pInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionName;
    } catch (PackageManager.NameNotFoundException exception) {
      Timber.e(exception, "Error getting app version");
    }
    return null;
  }

  /**
   * Provides the {@link Integer} with the app version code
   *
   * @param context {@link Context} of the app
   * @return {@link Integer} with the version code, or -1 if something went wrong
   */
  public static int getAppVersionCode(Context context) {

    PackageInfo pInfo = null;
    try {
      pInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException exception) {
      Timber.e(exception, "Error getting app version");
    }
    return -1;
  }
}
