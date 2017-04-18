package org.zalando.core.domain;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.zalando.core.exception.BaseThrowable;
import org.zalando.core.helper.CleaningHelper;

/**
 * Base class that will host common methods for all the domain service definitions
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
   * Provides an {@link io.reactivex.CompletableTransformer} to apply correct schedulers to
   * Completables
   *
   * @return {@link  io.reactivex.CompletableTransformer} that will apply correctly the right
   * schedulers
   */
  public CompletableTransformer applySchedulersToCompletable() {

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
   * Provides an {@link SingleTransformer} to apply correct schedulers to Singles
   *
   * @param <T> {@link T} type to create the transformer
   * @return {@link SingleTransformer} that will apply correctly the right schedulers
   */
  public <T> SingleTransformer<T, T> applySchedulersToSingle() {

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
   * Provides an {@link ObservableTransformer} to apply correct schedulers to Observables
   *
   * @param <T> {@link T} type to create the transformer
   * @return {@link ObservableTransformer} that will apply correctly the right schedulers
   */
  public <T> ObservableTransformer<T, T> applySchedulersToObservable() {

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
