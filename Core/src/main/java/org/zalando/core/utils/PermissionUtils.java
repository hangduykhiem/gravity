package org.zalando.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * Utility class to validate if runtime permissions are granted
 *
 * Created by jduran on 19/01/16.
 */
public class PermissionUtils {

  /**
   * Private constructor to avoid instance of object. Only static access to methods.
   */
  private PermissionUtils() {
  }

  /**
   * Checks if all the given permissions are granted for current application.
   *
   * @param applicationContext {@link Context} of the app
   * @param permissions {@link String} array with the permissions to check
   * @return {@link Boolean} indicating if all the permissions are granted
   */
  public static boolean checkRuntimePermissions(Context applicationContext, @NonNull String...
      permissions) {

    // Check if any of the permissions is not granted
    for (String permission : permissions) {

      if (!checkRuntimePermission(applicationContext, permission)) {
        return false;
      }
    }
    // All permissions are granted
    return true;
  }

  /**
   * Requests the runtime permissions provided
   *
   * @param activityRequestingPermission {@link Activity} where the result will be given
   * @param requestcode {@link Integer} with the request code provided by the Activity
   * @param permisions {@link String} with the permissions to request
   */
  public static void requestPermissions(Activity activityRequestingPermission, int requestcode,
      String... permisions) {

    ActivityCompat.requestPermissions(activityRequestingPermission, permisions, requestcode);
  }

  /**
   * Checks that a single permission is granted. Important to mock the static method.
   *
   * @param applicationContext {@link Context} of the application
   * @param permission {@link String} with the permission to check
   * @return {@link Boolean} indicating if runtime permission is allowed
   */
  private static boolean checkRuntimePermission(@NonNull Context applicationContext, @NonNull
      String permission) {

    return ActivityCompat.checkSelfPermission(applicationContext, permission)
        == PackageManager.PERMISSION_GRANTED;
  }

}
