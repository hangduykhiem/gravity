package fi.zalando.core.data;

import com.google.android.gms.common.api.GoogleApiClient;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fi.zalando.core.data.helper.GooglePlayServicesHelper;
import fi.zalando.core.data.helper.LocationHelper;
import fi.zalando.core.domain.helper.SubscriberAdapter;
import fi.zalando.core.exception.PermissionSecurityException;
import fi.zalando.core.exception.ServiceDisabledException;
import fi.zalando.core.utils.PermissionUtils;
import rx.Observable;
import rx.functions.Action1;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * Tests for {@link LocationRepository} class
 *
 * Created by jduran on 25/01/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PermissionUtils.class)
public class LocationRepositoryTest {

    private LocationRepository locationRepository;

    @Mock
    private GooglePlayServicesHelper googlePlayServicesHelper;
    @Mock
    private GoogleApiClient googleApiClient;
    @Mock
    private LocationManager locationManager;
    @Mock
    private LocationHelper locationHelper;

    @Before
    public void setup() {

        googlePlayServicesHelper = mock(GooglePlayServicesHelper.class);
        locationManager = mock(LocationManager.class);
        locationHelper = mock(LocationHelper.class);
        googleApiClient = mock(GoogleApiClient.class);

        locationRepository = new LocationRepository(mock(Context.class), googlePlayServicesHelper,
                locationManager, locationHelper);
    }

    @Test
    public void testGooglePlayServicesCorrectlyUsed() throws Exception {

        // Prepare Google Play Services Helper mock
        doAnswer(invocation -> Observable.just(googleApiClient)).when
                (googlePlayServicesHelper).loadGooglePlayServices(any(GoogleApiClient.class));
        doAnswer(invocation -> googleApiClient).when(googlePlayServicesHelper)
                .createLocationApiGoogleApiClient(any());

        // make a fake call
        locationRepository.loadCurrentLocation().toBlocking();
        // Check that load Google play services is executed
        verify(googlePlayServicesHelper, times(1)).loadGooglePlayServices(any(GoogleApiClient
                .class));
        // Check that we are loading google api client from the helper
        verify(googlePlayServicesHelper, times(1)).createLocationApiGoogleApiClient(any(Context
                .class));
    }

    @Test
    public void testLocationHelperUsedWhenGooglePlayServicesFailsForSingleRequest() throws
            Exception {

        // Prepare Google Play Services Helper mock
        doAnswer(invocation -> Observable.error(new Exception())).when
                (googlePlayServicesHelper).loadGooglePlayServices(any(GoogleApiClient.class));

        // Call load location
        locationRepository.loadCurrentLocation().toBlocking().subscribe(new SubscriberAdapter<>());
        // Verify that once google play services fails, location helper is used
        verify(locationHelper, times(1)).loadCurrentLocation();
    }

    @Test
    public void testLocationHelperUsedWhenGooglePlayServicesFailsFoLocationUpdatesRequest() throws
            Exception {

        // Prepare Google Play Services Helper mock
        doAnswer(invocation -> Observable.error(new Exception())).when
                (googlePlayServicesHelper).loadGooglePlayServices(any(GoogleApiClient.class));

        // Call loadLocations
        locationRepository.loadLocations(1000L, LocationHelper.LocationCriteria.ACCURACY_FINE)
                .toBlocking().subscribe(new SubscriberAdapter<>());
        // Verify that once google play services fails, location helper is used
        verify(locationHelper, times(1)).loadLocations(eq(1000L), eq(LocationHelper
                .LocationCriteria.ACCURACY_FINE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErrorWhenLocationPermissionNotAllowed() throws Exception {

        // Prepare Google Play Services Helper mock
        doAnswer(invocation -> Observable.just(googleApiClient)).when
                (googlePlayServicesHelper).loadGooglePlayServices(any(GoogleApiClient.class));
        doAnswer(invocation -> googleApiClient).when(googlePlayServicesHelper)
                .createLocationApiGoogleApiClient(any());

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(false).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));
        doReturn(false).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_COARSE_LOCATION));

        // Make location helper to crash also
        doAnswer(invocation -> Observable.error(new PermissionSecurityException(Manifest
                .permission.ACCESS_FINE_LOCATION))).when(locationHelper).loadCurrentLocation();

        // Mock on error handling
        Action1<Throwable> mockedOnErrorHandler = mock(Action1.class);
        locationRepository.loadCurrentLocation().toBlocking().subscribe(latLng -> {
            // no operation, exception should raise
        }, mockedOnErrorHandler);

        verify(mockedOnErrorHandler).call(eq(new PermissionSecurityException
                (Manifest.permission.ACCESS_FINE_LOCATION)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErrorWhenLocationServiceDisabled() throws Exception {

        // Prepare Google Play Services Helper mock
        doAnswer(invocation -> Observable.just(googleApiClient)).when
                (googlePlayServicesHelper).loadGooglePlayServices(any(GoogleApiClient.class));
        doAnswer(invocation -> googleApiClient).when(googlePlayServicesHelper)
                .createLocationApiGoogleApiClient(any());

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers from the permission check
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_FINE_LOCATION));
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(Manifest.permission.ACCESS_COARSE_LOCATION));

        // Mock answer from location manager, telling that location is disabled
        // Setup the mock
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .GPS_PROVIDER);
        doAnswer(invocation -> false).when(locationManager).isProviderEnabled(LocationManager
                .NETWORK_PROVIDER);

        // Make location helper to crash also
        doAnswer(invocation -> Observable.error(new ServiceDisabledException
                (ServiceDisabledException.ServiceType.LOCATION_SERVICES))).when(locationHelper)
                .loadCurrentLocation();

        // Mock on error handling
        Action1<Throwable> mockedOnErrorHandler = mock(Action1.class);
        locationRepository.loadCurrentLocation().toBlocking().subscribe(latLng -> {
            // no operation, exception should raise
        }, mockedOnErrorHandler);

        verify(mockedOnErrorHandler).call(eq(new ServiceDisabledException
                (ServiceDisabledException.ServiceType.LOCATION_SERVICES)));
    }

}
