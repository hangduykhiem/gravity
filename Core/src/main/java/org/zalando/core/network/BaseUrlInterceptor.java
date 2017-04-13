package org.zalando.core.network;

import org.zalando.core.utils.ValidationUtils;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Base class to let subclass interceptors to override retrofit host urls on the fly
 *
 * Created by jduran on 27/05/16.
 */
public abstract class BaseUrlInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {

    // Take current request from the chain
    Request request = chain.request();

    // Get the subclass defined url
    String hostUrl = getHostBaseUrl();
    if (ValidationUtils.isValidURL(hostUrl)) {

      // Create the new url, just taking the host from the
      // subclass url
      HttpUrl newUrl = request.url().newBuilder()
          .host(HttpUrl.parse(hostUrl).host())
          .build();

      // set the new url to the request
      request = request.newBuilder()
          .url(newUrl)
          .build();
    }

    return chain.proceed(request);
  }

  /**
   * Provide the url
   */
  public abstract String getHostBaseUrl();
}
