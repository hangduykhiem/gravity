package fi.zalando.core.utils;

import android.support.annotation.Nullable;

/**
 * Utility class to ease the check of equals when comparing objects
 *
 * Created by jduran on 11/01/16.
 */
public class EqualUtils {

    /**
     * Checks if two {@link Object}s are equal. Null safe.
     *
     * @param aThis {@link Object} to compare
     * @param aThat {@link Object} to compare
     * @return {@link Boolean} indicating if the objects are equal
     */
    public static boolean areEqual(@Nullable Object aThis, @Nullable Object aThat) {

        return aThis == null ? aThat == null : aThis.equals(aThat);
    }

}
