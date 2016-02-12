package fi.zalando.core.utils;

import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import fi.zalando.core.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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

        String invalidEmail = null;
        assertFalse(ValidationUtils.isValidEmail(invalidEmail));

        invalidEmail = "";
        assertFalse(ValidationUtils.isValidEmail(invalidEmail));

        invalidEmail = "invalid";
        assertFalse(ValidationUtils.isValidEmail(invalidEmail));

        invalidEmail = "invalid@invalid";
        assertFalse(ValidationUtils.isValidEmail(invalidEmail));

        invalidEmail = "invalid@invalid.";
        assertFalse(ValidationUtils.isValidEmail(invalidEmail));
    }

}
