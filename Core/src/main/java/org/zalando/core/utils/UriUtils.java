package org.zalando.core.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import java.util.Set;

/**
 * Utility class to help when using {@link android.net.Uri}
 */
public class UriUtils {

  /**
   * Private constructor to avoid object instances
   */
  private UriUtils() {
  }

  /**
   * Replaces the parameter matching given key with the given new value
   *
   * @param uri {@link Uri} source
   * @param key {@link String} with the parameter key
   * @param newValue {@link String} with the new value
   * @return {@link Uri} with the replaced parameter
   */
  public static Uri replaceUriParameter(@NonNull Uri uri,
      @NonNull String key,
      @NonNull String newValue) {

    final Set<String> params = uri.getQueryParameterNames();
    final Uri.Builder newUri = uri.buildUpon().clearQuery();
    for (String param : params) {
      String value;
      if (param.equals(key)) {
        value = newValue;
      } else {
        value = uri.getQueryParameter(param);
      }
      newUri.appendQueryParameter(param, value);
    }
    return newUri.build();
  }

}
