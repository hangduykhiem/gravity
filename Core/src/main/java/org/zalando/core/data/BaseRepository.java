package org.zalando.core.data;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import java.util.concurrent.TimeUnit;

/**
 * Base implementation of {@link org.zalando.core.data.BaseRepository}
 */
public abstract class BaseRepository {

  /**
   * Applies an exponential retrial logic to {@link Observable}
   *
   * @param maxRetries {@link Integer} with the maximum amount or retrials
   * @param initialDelayInMillis {@link Integer} with the initial delay in millis
   * @param <T> {@link T} type of the {@link Observable}
   * @return {@link ObservableTransformer} to modify the {@link Observable}
   */
  public <T> ObservableTransformer<T, T> applyExponentialBackoff(final int maxRetries,
      final int initialDelayInMillis) {
    return new ObservableTransformer<T, T>() {
      @Override
      public ObservableSource<T> apply(Observable<T> observable) {
        return observable
            .retryWhen(new RetryWithExponentialDelay(maxRetries, initialDelayInMillis));
      }
    };
  }

  /**
   * Class to apply to {@link Observable} an exponential retry backoff
   */
  private class RetryWithExponentialDelay implements
      Function<Observable<Throwable>, ObservableSource<?>> {

    private final int maxRetries;
    private int retryDelayMillis;
    private int retryCount;

    RetryWithExponentialDelay(final int maxRetries, final int retryDelayMillis) {
      this.maxRetries = maxRetries;
      this.retryDelayMillis = retryDelayMillis;
      this.retryCount = 0;

    }

    @Override
    public ObservableSource<?> apply(@NonNull Observable<Throwable> attempts) throws Exception {

      return attempts
          .flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
              if (++retryCount < maxRetries) {

                // apply the exponential backoff
                retryDelayMillis *= retryCount;

                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
              }

              // Max retries hit. Just pass the error along.
              return Observable.error(throwable);
            }
          });
    }
  }

}
