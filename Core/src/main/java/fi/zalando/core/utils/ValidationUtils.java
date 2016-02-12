package fi.zalando.core.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Utility class to help validating data
 *
 * Created by jduran on 12/02/16.
 */
public class ValidationUtils {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Private constructor to avoid object instance
     */
    private ValidationUtils() {
    }

    /**
     * Checks if the given {@link String} is a valid email
     *
     * @param emailToCheck {@link String} to check
     * @return True if valid email, false otherwise
     */
    public static boolean isValidEmail(String emailToCheck) {

        return !TextUtils.isEmpty(emailToCheck) && Pattern.compile(EMAIL_PATTERN).matcher
                (emailToCheck).matches();
    }

}
