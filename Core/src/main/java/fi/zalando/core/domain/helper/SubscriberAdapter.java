package fi.zalando.core.domain.helper;

/**
 * Base {@link rx.Subscriber} class whenever all methods implemented are required but only react to
 * one of them. To react to it, override needed function
 *
 * Created by jduran on 11/01/16.
 */
public class SubscriberAdapter<T> extends rx.Subscriber<T> {

    @Override
    public void onCompleted() {
        // no-op by default.
    }

    @Override
    public void onError(Throwable e) {
        // no-op by default.
    }

    @Override
    public void onNext(T t) {
        // no-op by default.
    }
}
