package fi.zalando.core.exception;

import fi.zalando.core.utils.ThrowableUtils;
import java.net.HttpURLConnection;
import retrofit2.HttpException;

/**
 * Base class to wrap errors thrown by the domain layer, so UI can react easily to errors
 *
 * Created by jduran on 16/06/16.
 */
public class BaseThrowable extends Throwable {

  /**
   * Type of the exception so it's easier to identify the error on UI level
   */
  public interface Type {

    int UNKNOWN = -1;

    // Network errors
    int NO_NETWORK = 0;
    int RECOVERABLE = 1;
    int AUTHORIZATION = 2;

    // platform errors
    int PERMISSION = 10;
    int SERVICE_DISABLED = 11;
  }

  private final Throwable sourceThrowable;
  private final int type;
  private final int code;

  /**
   * Constructor
   *
   * @param sourceThrowable {@link Throwable} that originated the error
   */
  public BaseThrowable(Throwable sourceThrowable) {

    this.sourceThrowable = sourceThrowable;

    // Get error code
    if (sourceThrowable instanceof HttpException) {
      code = ((HttpException) sourceThrowable).code();
    } else {
      code = 0;
    }

    // Parse the error with a known type

    // no connection
    if (sourceThrowable instanceof java.net.UnknownHostException) {
      type = Type.NO_NETWORK;
      return;
    }

    // Network errors
    if (sourceThrowable instanceof java.net.SocketTimeoutException
        || ThrowableUtils.isRetrofitHttpThrowable(sourceThrowable,
            HttpURLConnection.HTTP_INTERNAL_ERROR)
        || ThrowableUtils.isRetrofitHttpThrowable(sourceThrowable,
            HttpURLConnection.HTTP_UNAVAILABLE)
        ) {
      type = Type.RECOVERABLE;
      return;
    }
    if (ThrowableUtils.isRetrofitHttpThrowable(sourceThrowable,
        HttpURLConnection.HTTP_UNAUTHORIZED)) {
      type = Type.AUTHORIZATION;
      return;
    }

    // Platform errors
    if (sourceThrowable instanceof PermissionSecurityException) {
      type = Type.PERMISSION;
      return;
    }
    if (sourceThrowable instanceof ServiceDisabledException) {
      type = Type.SERVICE_DISABLED;
      return;
    }

    // Something else
    type = Type.UNKNOWN;
  }

  @Override
  public Throwable getCause() {
    return sourceThrowable;
  }

  /**
   * Returns the {@link Type} of the throwable
   *
   * @return {@link Integer} with the {@link Type} of the throwable
   */
  public int getType() {
    return type;
  }

  /**
   * Get the code of the throwable
   *
   * @return int
   */
  public int getCode() {
    return code;
  }


}
