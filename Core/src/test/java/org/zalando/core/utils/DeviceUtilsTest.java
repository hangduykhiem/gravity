package org.zalando.core.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import org.zalando.core.BuildConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Test class for {@link DeviceUtils}
 *
 * Created by jduran on 21/01/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
    "src/main/AndroidManifest.xml")
public class DeviceUtilsTest {

  @Mock
  private LocationManager locationManager;
  @Mock
  private TelephonyManager telephonyManager;

  @Before
  public void setup() {

    locationManager = mock(LocationManager.class);
    telephonyManager = mock(TelephonyManager.class);
  }

  @Test
  public void testGetUserCountry() {

    // Robolectric provides empty since it doesn't have sim
    assertTrue(TextUtils.isEmpty(DeviceUtils.getUserCountry(RuntimeEnvironment.application)));

    // mock get telephony manager from the context
    Context mockContext = mock(Context.class);
    doAnswer(invocation -> telephonyManager).when(mockContext).getSystemService(Context
        .TELEPHONY_SERVICE);

    // Setup the telephony manager mock
    doAnswer(invocation -> "ES").when(telephonyManager).getSimCountryIso();
    assertEquals(DeviceUtils.getUserCountry(mockContext), "ES".toLowerCase());

    // setup telephony manager mock when fetched from network country iso code
    doAnswer(invocation -> null).when(telephonyManager).getSimCountryIso();
    doAnswer(invocation -> TelephonyManager.PHONE_TYPE_GSM).when(telephonyManager)
        .getPhoneType();
    doAnswer(invocation -> "ES").when(telephonyManager).getNetworkCountryIso();
    assertEquals(DeviceUtils.getUserCountry(mockContext), "ES".toLowerCase());

    // setup telephony manager mock when PHONE_TYPE_CDMA phone
    doAnswer(invocation -> null).when(telephonyManager).getSimCountryIso();
    doAnswer(invocation -> TelephonyManager.PHONE_TYPE_CDMA).when(telephonyManager)
        .getPhoneType();
    doAnswer(invocation -> "ES").when(telephonyManager).getNetworkCountryIso();
    assertTrue(TextUtils.isEmpty(DeviceUtils.getUserCountry(mockContext)));

    // setup telephony manager mock when getNetworkCountryIso null
    doAnswer(invocation -> null).when(telephonyManager).getSimCountryIso();
    doAnswer(invocation -> TelephonyManager.PHONE_TYPE_CDMA).when(telephonyManager)
        .getPhoneType();
    doAnswer(invocation -> null).when(telephonyManager).getNetworkCountryIso();
    assertTrue(TextUtils.isEmpty(DeviceUtils.getUserCountry(mockContext)));
  }

  @Test
  public void testWhenGPSEnabled() {

    // Setup the mock
    doAnswer(invocation -> true).when(locationManager).isProviderEnabled(LocationManager
        .GPS_PROVIDER);
    doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
        .NETWORK_PROVIDER);

    assertTrue(DeviceUtils.isLocationEnabled(locationManager));
  }

  @Test
  public void testWhenNetworkEnabled() {

    // Setup the mock
    doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
        .GPS_PROVIDER);
    doAnswer(invocation -> true).when(locationManager).isProviderEnabled(LocationManager
        .NETWORK_PROVIDER);

    assertTrue(DeviceUtils.isLocationEnabled(locationManager));
  }

  @Test
  public void testWhenNoLocationEnabled() {

    // Setup the mock
    doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
        .GPS_PROVIDER);
    doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
        .NETWORK_PROVIDER);

    assertFalse(DeviceUtils.isLocationEnabled(locationManager));
  }

  @Test
  public void testDeviceScreenResolution() {

    Point testSize = DeviceUtils.getScreenResolution(RuntimeEnvironment.application);
    // Check that is not null
    assertNotNull(testSize);
    // Check that size is actually bigger than 0
    assertTrue(testSize.x > 0);
    assertTrue(testSize.y > 0);
  }

  @Test
  public void testAspectRatio() {

    float aspectRatio = DeviceUtils.getAspectRatio(RuntimeEnvironment.application);
    // Check that aspect ratio is actually bigger than 0
    assertTrue(aspectRatio > 0);
  }

  @Test
  public void testPxToDp() throws Exception {

    float testValue = 100f;
    float density = RuntimeEnvironment.application.getResources().getDisplayMetrics().density;
    // Check that density is valid:
    assertTrue(density > 0);
    float result = DeviceUtils.dpToPx(testValue);
    // Check that the result is valid:
    assertTrue(result == testValue * density);
  }

  @Test
  public void testDpToPx() throws Exception {

    float testValue = 100f;
    float density = RuntimeEnvironment.application.getResources().getDisplayMetrics().density;
    // Check that density is valid:
    assertTrue(density > 0);
    float result = DeviceUtils.pxToDp(testValue);
    // Check that the result is valid:
    assertTrue(result == testValue / density);
  }

  @Test
  public void testPxToSp() throws Exception {

    float testValue = 100f;
    float scaledDensity = RuntimeEnvironment.application.getResources().getDisplayMetrics()
        .scaledDensity;
    // Check that scaledDensity is valid:
    assertTrue(scaledDensity > 0);
    float result = DeviceUtils.spToPx(testValue);
    // Check that the result is valid:
    assertTrue(result == testValue * scaledDensity);
  }

  @Test
  public void testSpToPx() throws Exception {

    float testValue = 100f;
    float scaledDensity = RuntimeEnvironment.application.getResources().getDisplayMetrics()
        .scaledDensity;
    // Check that scaledDensity is valid:
    assertTrue(scaledDensity > 0);
    float result = DeviceUtils.pxToSp(testValue);
    // Check that the result is valid:
    assertTrue(result == testValue / scaledDensity);
  }
}
