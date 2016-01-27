package fi.zalando.core.domain.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeUnit;

import fi.zalando.core.BuildConfig;
import fi.zalando.core.data.LocationRepository;
import fi.zalando.core.domain.LocationService;
import fi.zalando.core.module.BaseDomainModule;
import rx.Observable;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test class for {@link LocationService}. Robolectric required for RxJava Android schedulers.
 *
 * Created by jduran on 27/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class LocationServiceTest {

    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Before
    public void setUp() {

        locationRepository = mock(LocationRepository.class);
        locationService = new BaseDomainModule().provideLocationService(locationRepository);
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
                ());
        // Make the request
        locationService.loadLocations(10L, TimeUnit.SECONDS);
        // Check that mocked loadLocations is used with proper time calculation
        verify(locationRepository).loadLocations(TimeUnit.SECONDS.toMillis(10L));
    }
}
