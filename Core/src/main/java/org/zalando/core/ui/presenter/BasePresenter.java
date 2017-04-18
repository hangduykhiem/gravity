package org.zalando.core.ui.presenter;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import org.zalando.core.domain.helper.DisposableHelper;
import org.zalando.core.ui.view.BaseView;
import org.zalando.core.utils.Preconditions;

/**
 * Class responsible of holding common methods for all Presenters in the App.
 */
public abstract class BasePresenter<T extends BaseView> {

  protected T view;
  private boolean isViewSet;
  private boolean isPresenterInitialised;

  /**
   * Injected objects
   */
  protected DisposableHelper disposableHelper;

  /**
   * Constructor
   */
  protected BasePresenter(DisposableHelper disposableHelper) {

    this.disposableHelper = disposableHelper;
  }

  /**
   * Provides the {@link DisposableHelper} instance linked with the {@link BasePresenter}
   *
   * @return {@link DisposableHelper} instance
   */
  public DisposableHelper getDisposableHelper() {

    return disposableHelper;
  }

  /**
   * Initialises the presenter. Linked to Activity onCreate() and Fragment onActivityCreated
   * lifecycle methods
   *
   * @param state {@link Bundle} to restore or start the instance of the presenter
   */
  @CallSuper
  public void initialise(@NonNull Bundle state) {

    Preconditions.checkState(isViewSet, "Call setView before initialising presenter");
    isPresenterInitialised = true;
    // Init View
    view.initView(state);
  }

  /**
   * Saves the instance of the {@link BasePresenter}
   *
   * @param outState {@link Bundle} to save the state
   */
  @SuppressWarnings("UnusedParameters")
  @CallSuper
  public void onSaveInstanceState(Bundle outState) {

  }

  /**
   * Gets a callback when the Fragment/Activity where the presenter is linked appears on the
   * screen
   */
  @CallSuper
  public void resume() {

    Preconditions.checkState(isViewSet, "Call setView before resuming presenter");
    Preconditions.checkState(isPresenterInitialised, "Call initialise before resuming presenter");
  }

  /**
   * Release the {@link BasePresenter}
   */
  @CallSuper
  public void destroy() {

    disposableHelper.clear();
  }

  /**
   * Sets the {@link T} instance to link the {@link BasePresenter} with the UI so that the
   * presenter can hold a reference to the view it will be interacting with.
   *
   * @param view {@link T} to link the {@link BasePresenter} with the UI
   */
  public final void setView(@NonNull T view) {

    Preconditions.checkNotNull(view);
    isViewSet = true;
    this.view = view;
  }

}
