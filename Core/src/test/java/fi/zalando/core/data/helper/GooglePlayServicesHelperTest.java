package fi.zalando.core.data.helper;

import com.google.android.gms.location.LocationServices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link GooglePlayServicesHelper} class
 *
 * Created by jduran on 18/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GooglePlayServicesHelperTest {

    private GooglePlayServicesHelper googlePlayServicesHelper;

    @Mock
    private ReactiveLocationProvider mockedReactiveLocationProvider;

    @Before
    public void setup() {

        mockedReactiveLocationProvider = mock(ReactiveLocationProvider.class);

        // Setup a dummy answer from ReactiveLocationProvider so the test will not crash
        doAnswer(invocation -> Observable.never()).when(mockedReactiveLocationProvider)
                .getUpdatedLocation(any());

        googlePlayServicesHelper = new GooglePlayServicesHelper(mockedReactiveLocationProvider);
    }

    @Test
    public void testGetGoogleApiClientObservableUsed() {

        // Make a fake subscription
        googlePlayServicesHelper.loadGooglePlayServices(LocationServices.API);
        // Verify needed methods are called
        verify(mockedReactiveLocationProvider, times(1)).getGoogleApiClientObservable(eq
                (LocationServices.API));
    }

}
