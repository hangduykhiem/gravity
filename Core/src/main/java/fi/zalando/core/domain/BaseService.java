package fi.zalando.core.domain;

import fi.zalando.core.helper.CleaningHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
     * Executes the cleaning tasks. Override if needed
     */
    @Override
    public void clean() {

    }

}
