package org.zalando.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.Build;
import org.zalando.core.BuildConfig;
import org.zalando.core.domain.helper.DisposableHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Tests for {@link DisposableHelper} class
 *
 * Created by jduran on 03/12/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = PlatformUtilsTest.SDK_LEVEL_TO_TEST, manifest =
    "src/main/AndroidManifest.xml")
public class PlatformUtilsTest {

  public static final int SDK_LEVEL_TO_TEST = Build.VERSION_CODES.JELLY_BEAN;

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
