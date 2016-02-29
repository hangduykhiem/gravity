package fi.zalando.core.domain;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Base class that will host common methods for all the domain service definitions
 *
 * Created by jduran on 30/11/15.
 */
public abstract class BaseService {

    /**
     * Provides an {@link rx.Observable.Transformer} to apply correct schedulers
     *
     * @param <T> {@link T} type to create the transformer
     * @return {@link rx.Observable.Transformer} that will apply correctly the right schedulers
     */
    public <T> Observable.Transformer<T, T> applySchedulers() {

        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread());
    }

    /**
     * Creates a {@link BehaviorSubject} to use within services using the given {@link Observable}
     * as a reference in order to cache results for faster delivery
     *
     * @param observable {@link Observable} to use as reference in the {@link Subject}
     * @param <T>        {@link T} of the {@link Observable}
     * @return {@link Subject} that listens and emits the given {@link Observable}
     */
    protected <T> Subject<T, T> createObservingSubject(Observable<T> observable) {

        Subject<T, T> behaviorSubject = BehaviorSubject.create();
        observable.subscribe(behaviorSubject::onNext);
        return behaviorSubject;
    }

}
