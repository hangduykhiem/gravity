package fi.zalando.core.domain;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    protected <T> Observable.Transformer<T, T> applySchedulers() {

        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread());
    }

}
