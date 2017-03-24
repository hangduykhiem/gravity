package fi.zalando.core.utils;

/**
 * Utility class to help validating data
 *
 * Created by jduran on 12/02/16.
 */
public class ValidationUtils {

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

    return !TextUtils.isEmpty(emailToCheck) && Patterns.EMAIL_ADDRESS.matcher(
        emailToCheck).matches();
  }

  /**
   * Checks if the given {@link String} is a valid url
   *
   * @param urlToCheck {@link String} to check
   * @return True if valid url, false otherwise
   */
  public static boolean isValidURL(String urlToCheck) {

    return !TextUtils.isEmpty(urlToCheck) && Patterns.WEB_URL.matcher(urlToCheck).matches();
  }

}
