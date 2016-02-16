package fi.zalando.core.data;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import fi.zalando.core.BuildConfig;
import fi.zalando.core.data.helper.LocationHelper;
import fi.zalando.core.domain.helper.SubscriberAdapter;
import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link LocationRepository} class
 *
 * Created by jduran on 25/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
        "src/main/AndroidManifest.xml")
public class LocationRepositoryTest {

    private LocationRepository locationRepository;

    @Mock
    private ReactiveLocationProvider reactiveLocationProvider;
    @Mock
    private LocationHelper locationHelper;

    @Before
    public void setup() {

        reactiveLocationProvider = mock(ReactiveLocationProvider.class);
        locationHelper = mock(LocationHelper.class);

        locationRepository = new LocationRepository(reactiveLocationProvider, locationHelper);
    }

    @Test
    public void testLoadCurrentLocation() {

        final double lat = 1f;
        final double lon = 2f;

        // Prepare mock reactiveLocationProvider
        final LatLng latLng = new LatLng(lat, lon);
        final Location location = new Location("mockedProvider");
        location.setLatitude(lat);
        location.setLongitude(lon);

        doAnswer(invocation -> Observable.just(location)).when(reactiveLocationProvider)
                .getLastKnownLocation();

        // verify location is provided correctly
        LatLng providedLatLng = locationRepository.loadCurrentLocation().toBlocking().first();

        assertEquals(latLng, providedLatLng);
    }

    @Test
    public void testLoadCurrentLocationUsingHelperWhenFails() {

        doAnswer(invocation -> Observable.error(new Exception())).when(reactiveLocationProvider)
                .getLastKnownLocation();

        locationRepository.loadCurrentLocation().subscribe(new SubscriberAdapter<>());

        verify(locationHelper, times(1)).loadCurrentLocation();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadLocationsFailsWhenAccuracyNotRecognised() {

        locationRepository.loadLocations(0L, Integer.MAX_VALUE);
    }

    @Test
    public void loadLocationsCalled() {

        // Setup locationRepository mock  to return something when loadLocations is called
        // If not, test will crash with null pointer
        doAnswer(invocation -> Observable.never()).when(reactiveLocationProvider)
                .getUpdatedLocation(any());

        final int accuracyCriteria = LocationRequest.PRIORITY_HIGH_ACCURACY;
        final long locationUpdateFrequency = 5000L;

        // Build location request adding user defined settings
        LocationRequest locationRequest = LocationRequest.create().setPriority(accuracyCriteria)
                .setInterval(locationUpdateFrequency);

        locationRepository.loadLocations(locationUpdateFrequency, accuracyCriteria).subscribe(new
                SubscriberAdapter<>());

        verify(reactiveLocationProvider).getUpdatedLocation(eq(locationRequest));
    }

    @Test
    public void loadLocationsCalledUsingHelperWhenFails() {

        final int accuracyCriteria = LocationRequest.PRIORITY_HIGH_ACCURACY;
        final long locationUpdateFrequency = 5000L;

        // force location provider to fail
        doAnswer(invocation -> Observable.error(new Exception())).when(reactiveLocationProvider)
                .getUpdatedLocation(any());

        locationRepository.loadLocations(locationUpdateFrequency, accuracyCriteria).subscribe(new
                SubscriberAdapter<>());

        verify(locationHelper, times(1)).loadLocations(locationUpdateFrequency, accuracyCriteria);
    }

}
