package fi.zalando.core.domain;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.TimeUnit;

import fi.zalando.core.data.LocationRepository;
import fi.zalando.core.test.TestUtils;
import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Test class for {@link LocationService}. Robolectric required for RxJava Android schedulers.
 *
 * Created by jduran on 27/01/16.
 */
public class LocationServiceTest {

    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Before
    public void setUp() {

        locationRepository = mock(LocationRepository.class);
        locationService = spy(new LocationService(locationRepository));
        TestUtils.setupMockedDomainService(locationService);
    }

    @Test
    public void testLoadCurrentLocationIsUsed() {

        // Setup LocationRepository mock  to return something when loadDigest is called
        // If not, test will crash
        doAnswer(invocation -> Observable.never()).when(locationRepository).loadCurrentLocation();
        // Make the request
        locationService.loadCurrentLocation();
        // Check that mocked loadCurrentLocation is used
        verify(locationRepository).loadCurrentLocation();
    }

    @Test
    public void testLoadLocationsIsUsed() {

        // Setup LocationRepository mock  to return something when loadDigest is called
        // If not, test will crash
        doAnswer(invocation -> Observable.never()).when(locationRepository).loadLocations(anyLong
                (), anyInt());
        // Make the request
        locationService.loadLocations(10L, TimeUnit.SECONDS, LocationRequest
                .PRIORITY_HIGH_ACCURACY);
        // Check that mocked loadLocations is used with proper time calculation
        verify(locationRepository).loadLocations(TimeUnit.SECONDS.toMillis(10L), LocationRequest
                .PRIORITY_HIGH_ACCURACY);
    }
}
