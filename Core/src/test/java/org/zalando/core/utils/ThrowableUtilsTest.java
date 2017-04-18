package org.zalando.core.utils;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.net.HttpURLConnection;
import okhttp3.ResponseBody;
import org.junit.Test;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Test class for {@link ThrowableUtils}
 */
public class ThrowableUtilsTest {

  @Test
  public void testIsRetrofitHttpThrowable() {

    // Build a mock response
    Response response = Response.error(404, mock(ResponseBody.class));
    // Check that the comparison is right when 404
    assertTrue(ThrowableUtils.isRetrofitHttpThrowable(new HttpException(response),
        HttpURLConnection.HTTP_NOT_FOUND));
    // Check that returns false when wrong number
    assertFalse(ThrowableUtils.isRetrofitHttpThrowable(new HttpException(response),
        HttpURLConnection.HTTP_UNAUTHORIZED));
    // Check that returns false when another exception is provided
    assertFalse(ThrowableUtils.isRetrofitHttpThrowable(new Exception(), HttpURLConnection
        .HTTP_NOT_FOUND));
  }

}
