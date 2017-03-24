package fi.zalando.core.utils;

import android.support.annotation.Nullable;

public class TextUtils {

  public static boolean isEmpty(@Nullable CharSequence str) {
    return str == null || str.length() == 0;
  }
}
