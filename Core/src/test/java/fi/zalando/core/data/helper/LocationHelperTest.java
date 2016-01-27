package fi.zalando.core.data.helper;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fi.zalando.core.exception.PermissionSecurityException;
import fi.zalando.core.exception.ServiceDisabledException;
import fi.zalando.core.utils.PermissionUtils;
import rx.functions.Action1;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * Tests for {@link LocationHelper} class
 *
 * Created by jduran on 25/01/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PermissionUtils.class})
public class LocationHelperTest {

    private LocationHelper locationHelper;

    @Mock
    private LocationManager locationManager;
    @Mock
    private Context context;
    @Mock
    private Criteria criteria;

    @Before
    public void setup() {

        locationManager = mock(LocationManager.class);
        context = mock(Context.class);
        criteria = mock(Criteria.class);

        // Prepare context mock
        doAnswer(invocation -> context).when(context).getApplicationContext();

        // Create helper to test
        locationHelper = new LocationHelper(context, locationManager, criteria);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErrorWhenLocationPermissionNotAllowed() throws Exception {

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(false).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));

        // Mock on error handling
        Action1<Throwable> mockedOnErrorHandler = mock(Action1.class);
        locationHelper.loadCurrentLocation().toBlocking().subscribe(latLng -> {
            // no operation, exception should raise
        }, mockedOnErrorHandler);

        verify(mockedOnErrorHandler).call(eq(new PermissionSecurityException(Manifest.permission
                .ACCESS_FINE_LOCATION)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErrorWhenLocationServiceDisabled() throws Exception {

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));

        // Mock answer from location manager, telling that location is disabled
        // Setup the mock
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .GPS_PROVIDER);
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .NETWORK_PROVIDER);

        // Mock on error handling
        Action1<Throwable> mockedOnErrorHandler = mock(Action1.class);
        locationHelper.loadCurrentLocation().toBlocking().subscribe(latLng -> {
            // no operation, exception should raise
        }, mockedOnErrorHandler);

        verify(mockedOnErrorHandler).call(eq(new ServiceDisabledException
                (ServiceDisabledException.ServiceType.LOCATION_SERVICES)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRequestSingleUpdateForCurrentLocationWhenAllPermissionsAndServicesOn() throws
            Exception {

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));

        // Setup the mock saying that location services are up and running
        doAnswer(invocation -> true).when(locationManager).isProviderEnabled(LocationManager
                .GPS_PROVIDER);
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .NETWORK_PROVIDER);

        // Subscribe to fetch the location
        locationHelper.loadCurrentLocation().subscribe();

        verify(locationManager, times(1)).requestSingleUpdate(eq(criteria), any(LocationListener
                .class), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRequestMultipleUpdatesForCurrentLocationWhenAllPermissionsAndServicesOn() throws
            Exception {

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));

        // Setup the mock saying that location services are up and running
        doAnswer(invocation -> true).when(locationManager).isProviderEnabled(LocationManager
                .GPS_PROVIDER);
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .NETWORK_PROVIDER);

        // Subscribe to fetch the location
        locationHelper.loadLocations(1000L).subscribe();

        verify(locationManager, times(1)).requestLocationUpdates(eq(1000L), anyFloat(), eq
                (criteria), any(LocationListener.class), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveUpdatesAfterUnsubscribingSubscriberWhenRequestingLastLocation() throws
            Exception {

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));

        // Setup the mock saying that location services are up and running
        doAnswer(invocation -> true).when(locationManager).isProviderEnabled(LocationManager
                .GPS_PROVIDER);
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .NETWORK_PROVIDER);

        // Subscribe to fetch the location and just unsubscribe right away
        locationHelper.loadCurrentLocation().subscribe().unsubscribe();

        verify(locationManager, times(1)).removeUpdates(any(LocationListener.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveUpdatesAfterUnsubscribingSubscriberWhenRequestingLocationUpdates() throws
            Exception {

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));

        // Setup the mock saying that location services are up and running
        doAnswer(invocation -> true).when(locationManager).isProviderEnabled(LocationManager
                .GPS_PROVIDER);
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .NETWORK_PROVIDER);

        // Subscribe to fetch the location and just unsubscribe right away
        locationHelper.loadLocations(1000L).subscribe().unsubscribe();

        verify(locationManager, times(1)).removeUpdates(any(LocationListener.class));
    }

}
