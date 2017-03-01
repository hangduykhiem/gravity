package fi.zalando.core.utils;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Class to help with RxJava operations
 *
 * Created by jduran on 17/07/16.
 */
public class RxUtils {

    /**
     * Private constructor to avoid object instance
     */
    private RxUtils() {

    }

    /**
     * Creates a {@link BehaviorSubject} to use within domain services using the given {@link
     * Observable} as a reference
     *
     * @param observable {@link Observable} to use as reference in the {@link Subject}
     * @param <T>        {@link T} of the {@link Observable}
     * @return {@link Subject} that listens and emits the given {@link Observable}
     */
    public static <T> Observable<T> createObservingSubject(Observable<T> observable) {

        Subject<T> behaviorSubject = BehaviorSubject.create();
        observable.subscribe(behaviorSubject::onNext, behaviorSubject::onError);
        return behaviorSubject;
    }

}
