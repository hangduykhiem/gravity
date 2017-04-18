package org.zalando.core.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Utility class to launch intent for different purposes
 */
public class IntentUtil {

  // Private constructor to force to use static methods
  private IntentUtil() {
  }

  /**
   * Method that launches an intent to send an email
   *
   * @param launchingActivity {@link Activity} that is launching
   * @param emailAddress {@link String} with the destination email address
   * @param chooserText {@link String} with the chooser text. Ex: Please, select your email client
   * @param subject {@link String} to fill the subject of the email. Null if not needed.
   * @param body {@link String} to fill the body of the email. Null if not needed.
   */
  public static void sendEmail(Activity launchingActivity,
      String emailAddress,
      String chooserText,
      @Nullable String subject,
      @Nullable String body) {

    // Check compulsory fields
    Preconditions.checkArgument(ValidationUtils.isValidEmail(emailAddress), "Recipient email "
        + "is invalid");
    Preconditions.checkArgument(!TextUtils.isEmpty(chooserText), "Chooser text is empty.");

    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emailAddress, null));
    i.putExtra(Intent.EXTRA_SUBJECT, subject);
    i.putExtra(Intent.EXTRA_TEXT, body);
    launchingActivity.startActivity(Intent.createChooser(i, chooserText));
  }

}
