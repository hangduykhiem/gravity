package fi.zalando.core.utils;

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
     * Checks if the given permissions are granted for current application.
     *
     * @param applicationContext {@link Context} of the app
     * @param permissions        {@link String} array with the permissions to check
     * @return {@link Boolean} indicating if all the permissions are granted
     */
    public static boolean checkRuntimePermissions(Context applicationContext, @NonNull String...
            permissions) {

        // Check if any of the permissions is not granted
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(applicationContext, permission) ==
                    PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        // All permissions are granted
        return true;
    }

}
