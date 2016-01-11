package fi.zalando.core.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import fi.zalando.core.BuildConfig;
import fi.zalando.core.domain.helper.SubscriptionHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SubscriptionHelper} class
 *
 * Created by jduran on 03/12/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = PlatformUtilsTest.SDK_LEVEL_TO_TEST, manifest =
        "src/main/AndroidManifest.xml")
public class PlatformUtilsTest {

    public static final int SDK_LEVEL_TO_TEST = 16;

    @Test
    public void testGetOSVersion() {

        // Check that the set SDK level in the Robolectric
        // configuration gives correct answer
        assertEquals(PlatformUtils.getOSVersion(), SDK_LEVEL_TO_TEST);
    }

    @Test
    public void testIsNewerOrEqualSDKVersion() {

        // Check that the the method says that the used SDK is newer than 1 less
        assertTrue(PlatformUtils.isNewerOrEqualSDKVersion(SDK_LEVEL_TO_TEST - 1));
        // Check that the the method says that the used SDK is at least the same as the used one
        assertTrue(PlatformUtils.isNewerOrEqualSDKVersion(SDK_LEVEL_TO_TEST));
        // Check if it says that the SDK is NOT newer
        assertFalse(PlatformUtils.isNewerOrEqualSDKVersion(SDK_LEVEL_TO_TEST + 1));
    }

}
