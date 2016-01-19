package fi.zalando.core.data.helper;

import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.zalando.core.module.BaseHelperModule;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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
    private GoogleApiClient mockedGoogleApiClient;

    @Before
    public void setup() {

        googlePlayServicesHelper = new BaseHelperModule().provideGooglePlayServicesHelper();
        mockedGoogleApiClient = mock(GoogleApiClient.class);
    }

    @Test
    public void testCallbacksRegistered() {

        // Make a fake subscription
        googlePlayServicesHelper.loadGooglePlayServices(mockedGoogleApiClient).subscribe();
        // Verify needed methods are called
        verify(mockedGoogleApiClient).registerConnectionCallbacks(any
                (GoogleApiClient.ConnectionCallbacks.class));
        verify(mockedGoogleApiClient).registerConnectionFailedListener(any
                (GoogleApiClient.OnConnectionFailedListener.class));
    }

    @Test
    public void testConnectCalled() {

        // Make a fake subscription
        googlePlayServicesHelper.loadGooglePlayServices(mockedGoogleApiClient).subscribe();
        // Verify connect is called
        verify(mockedGoogleApiClient).connect();
    }

    @Test
    public void testGoogleApiReturnedWhenConnected() {

        // Setup persistence mock. Return the hardcoded date
        doAnswer(invocation -> true).when(mockedGoogleApiClient).isConnected();
        // Verify google api client is returned if connected succesfully
        assertEquals(googlePlayServicesHelper.loadGooglePlayServices(mockedGoogleApiClient)
                .toBlocking().first(), mockedGoogleApiClient);
    }

    // TODO Investigate how to test connection failures, etc. Check -> http://bit.ly/1U6YQ09

}
