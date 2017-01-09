package fi.zalando.core.domain;

import fi.zalando.core.exception.BaseThrowable;
import fi.zalando.core.helper.CleaningHelper;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Base class that will host common methods for all the domain service definitions
 *
 * Created by jduran on 30/11/15.
 */
public abstract class BaseService implements CleaningHelper.Cleanable {

    /**
     * Default constructor, not making use of cleaning helper at all.
     */
    protected BaseService() {

    }

    /**
     * Constructor to enforce adding itself to {@link CleaningHelper}
     *
     * @param cleaningHelper {@link CleaningHelper} to add itself to
     */
    protected BaseService(CleaningHelper cleaningHelper) {

        cleaningHelper.addCleanables(this);
    }

    /**
     * Provides an {@link rx.Completable.Transformer} to apply correct schedulers to Completables
     *
     * @return {@link rx.Completable.Transformer} that will apply correctly the right schedulers
     */
    public Completable.Transformer applySchedulersToCompletable() {

        return completable ->
                completable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(throwable -> {
                            if (!(throwable instanceof BaseThrowable)) {
                                return Completable.error(new BaseThrowable(throwable));
                            } else {
                                return Completable.error(throwable);
                            }
                        });
    }

    /**
     * Provides an {@link rx.Single.Transformer} to apply correct schedulers to Singles
     *
     * @param <T> {@link T} type to create the transformer
     * @return {@link rx.Single.Transformer} that will apply correctly the right schedulers
     */
    public <T> Single.Transformer<T, T> applySchedulersToSingle() {

        return completable ->
                completable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(throwable -> {
                            if (!(throwable instanceof BaseThrowable)) {
                                return Single.error(new BaseThrowable(throwable));
                            } else {
                                return Single.error(throwable);
                            }
                        });
    }

    /**
     * Provides an {@link rx.Observable.Transformer} to apply correct schedulers to Observables
     *
     * @param <T> {@link T} type to create the transformer
     * @return {@link rx.Observable.Transformer} that will apply correctly the right schedulers
     */
    public <T> Observable.Transformer<T, T> applySchedulersToObservable() {

        return observable ->
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(throwable -> {
                            if (!(throwable instanceof BaseThrowable)) {
                                return Observable.error(new BaseThrowable(throwable));
                            } else {
                                return Observable.error(throwable);
                            }
                        });
    }

    /**
     * Executes the cleaning tasks. Override if needed
     */
    @Override
    public void clean() {

    }

}
