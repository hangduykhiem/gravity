package fi.zalando.core.domain.helper;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Base {@link Observer} class whenever all methods implemented are required but only react to one
 * of them. To react to it, override needed function
 *
 * Created by jduran on 11/01/16.
 */
public class SubscriberAdapter<T> implements Observer<T> {

  @Override
  public void onSubscribe(Disposable d) {
    // no-op by default.
  }

  @Override
  public void onNext(T t) {
    // no-op by default.
  }

  @Override
  public void onError(Throwable e) {
    // no-op by default.
  }

  @Override
  public void onComplete() {
    // no-op by default.
  }
}
