package fi.zalando.core.utils;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import android.os.Build;
import fi.zalando.core.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test class for {@link ValidationUtils}
 *
 * Created by jduran on 12/02/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
    "src/main/AndroidManifest.xml")
public class ValidationUtilsTest {

  @Test
  public void testValidEmail() {

    String validEmail = "name@domain.com";
    assertTrue(ValidationUtils.isValidEmail(validEmail));

    validEmail = "name@domain.fashion";
    assertTrue(ValidationUtils.isValidEmail(validEmail));
  }

  @Test
  public void testInvalidEmail() {

    assertFalse(ValidationUtils.isValidEmail(null));

    String invalidEmail = "";
    assertFalse(ValidationUtils.isValidEmail(invalidEmail));

    invalidEmail = "invalid";
    assertFalse(ValidationUtils.isValidEmail(invalidEmail));

    invalidEmail = "invalid@invalid";
    assertFalse(ValidationUtils.isValidEmail(invalidEmail));

    invalidEmail = "invalid@invalid.";
    assertFalse(ValidationUtils.isValidEmail(invalidEmail));

    invalidEmail = "invalid@invalid.invalid@invalid.com";
    assertFalse(ValidationUtils.isValidEmail(invalidEmail));
  }

  @Test
  public void testValidUrl() {

    String validUrl = "http://www.google.com";
    assertTrue(ValidationUtils.isValidURL(validUrl));

    validUrl = "http://www.google.com/another?param=aParam";
    assertTrue(ValidationUtils.isValidURL(validUrl));

    validUrl = "www.invalid.com";
    assertTrue(ValidationUtils.isValidURL(validUrl));

    validUrl = "invalid.com";
    assertTrue(ValidationUtils.isValidURL(validUrl));
  }

  @Test
  public void testInvalidUrl() {

    assertFalse(ValidationUtils.isValidURL(null));

    String invalidUrl = "";
    assertFalse(ValidationUtils.isValidURL(invalidUrl));

    invalidUrl = "invalid";
    assertFalse(ValidationUtils.isValidURL(invalidUrl));

    invalidUrl = "http://invalid";
    assertFalse(ValidationUtils.isValidURL(invalidUrl));
  }

}
