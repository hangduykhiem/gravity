package fi.zalando.core.utils;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import fi.zalando.core.BuildConfig;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@link ValidationUtils}
 *
 * Created by jduran on 12/02/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
        "src/main/AndroidManifest.xml")
public class ValidationUtilsTest {

    @Test
    public void testValidEmail() {

        String validEmail = "name@domain.com";
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
    }

}
