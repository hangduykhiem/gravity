package fi.zalando.core.utils;

import android.graphics.Point;
import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import fi.zalando.core.BuildConfig;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Test class for {@link DeviceUtils}
 *
 * Created by jduran on 21/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16, manifest = "src/main/AndroidManifest.xml")
public class DeviceUtilsTest {

    @Mock
    private LocationManager locationManager;

    @Before
    public void setup() {

        locationManager = mock(LocationManager.class);
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

        Point testSize = DeviceUtils.screenResolution(RuntimeEnvironment.application);
        // Check that is not null
        assertNotNull(testSize);
        // Check that size is actually bigger than 0
        assertTrue(testSize.x > 0);
        assertTrue(testSize.y > 0);
    }

    @Test
    public void testPxToDp() throws Exception {
        float result = DeviceUtils.dpToPx(RuntimeEnvironment.application, 100f);
        assertTrue(result > 0);
    }

    @Test
    public void testDpToPx() throws Exception {
        float result = DeviceUtils.pxToDp(RuntimeEnvironment.application, 100f);
        assertTrue(result > 0);
    }
}
