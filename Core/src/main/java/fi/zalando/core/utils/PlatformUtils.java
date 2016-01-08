package fi.zalando.core.utils;

/**
 * Utility class to provide Android platform related information
 *
 * Created by jduran on 27/10/14.
 */
public class PlatformUtils {

    /**
     * Returns the currently running OS version SDK version on the phone
     *
     * @return Running SDK Version
     */
    public static int getOSVersion() {

        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * Returns the currently running OS version SDK version on the phone is newer or equal than the
     * given one in the parameter
     *
     * @param version Int value of the SDK version to check {@link android.os.Build.VERSION_CODES}
     * @return If running version is newer or equal than the given one
     */
    public static boolean isNewerOrEqualSDKVersion(int version) {

        return getOSVersion() >= version;
    }

}
