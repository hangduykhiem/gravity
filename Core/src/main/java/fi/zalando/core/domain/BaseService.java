package fi.zalando.core.domain;

import fi.zalando.core.helper.CleaningHelper;
import rx.Completable;
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
public abstract class BaseService implements CleaningHelper.Cleanable {

    /**
     * Constructor to enforce adding itself to {@link CleaningHelper}
     *
     * @param cleaningHelper {@link CleaningHelper} to add itself to
     */
    protected BaseService(CleaningHelper cleaningHelper) {

        cleaningHelper.addCleanables(this);
    }

    /**
     * Provides an {@link rx.Observable.Transformer} to apply correct schedulers to Completables
     *
     * @return {@link rx.Observable.Transformer} that will apply correctly the right schedulers
     */
    public Completable.CompletableTransformer applySchedulersToCompletable() {

        return completable -> completable.subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread());
    }

    /**
     * Provides an {@link rx.Observable.Transformer} to apply correct schedulers to Observables
     *
     * @param <T> {@link T} type to create the transformer
     * @return {@link rx.Observable.Transformer} that will apply correctly the right schedulers
     */
    public <T> Observable.Transformer<T, T> applySchedulersToObservable() {

        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Creates a {@link BehaviorSubject} to use within domain services using the given {@link
     * Observable} as a reference
     *
     * @param observable {@link Observable} to use as reference in the {@link Subject}
     * @param <T>        {@link T} of the {@link Observable}
     * @return {@link Subject} that listens and emits the given {@link Observable}
     */
    protected <T> Observable<T> createObservingSubject(Observable<T> observable) {

        Subject<T, T> behaviorSubject = BehaviorSubject.create();
        observable.subscribe(behaviorSubject::onNext, behaviorSubject::onError);
        return behaviorSubject;
    }

    /**
     * Executes the cleaning tasks. Override if needed
     */
    @Override
    public void clean() {

    }

}
