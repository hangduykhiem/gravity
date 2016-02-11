package fi.zalando.core.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.HttpURLConnection;

import retrofit2.HttpException;
import retrofit2.Response;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Test class for {@link ThrowableUtils}
 *
 * Created by jduran on 11/02/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Response.class)
public class ThrowableUtilsTest {

    @Test
    public void testIsRetrofitHttpThrowable() {

        // Build a mock response
        Response mockResponse = mock(Response.class);
        doAnswer(invocation -> 404).when(mockResponse).code();
        // Check that the comparison is right when 404
        assertTrue(ThrowableUtils.isRetrofitHttpThrowable(new HttpException(mockResponse),
                HttpURLConnection.HTTP_NOT_FOUND));
        // Check that returns false when wrong number
        assertFalse(ThrowableUtils.isRetrofitHttpThrowable(new HttpException(mockResponse),
                HttpURLConnection.HTTP_UNAUTHORIZED));
        // Check that returns false when another exception is provided
        assertFalse(ThrowableUtils.isRetrofitHttpThrowable(new Exception(), HttpURLConnection
                .HTTP_NOT_FOUND));
    }

}
