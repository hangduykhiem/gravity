package fi.zalando.core.utils;

import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Test class for {@link DeviceUtils}
 *
 * Created by jduran on 21/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
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
    public void testWhenNothingEnabled() {

        // Setup the mock
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .GPS_PROVIDER);
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .NETWORK_PROVIDER);

        assertFalse(DeviceUtils.isLocationEnabled(locationManager));
    }

}
