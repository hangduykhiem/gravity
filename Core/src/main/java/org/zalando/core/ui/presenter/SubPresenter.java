package org.zalando.core.ui.presenter;

import android.support.annotation.CallSuper;
import org.zalando.core.domain.helper.DisposableHelper;
import org.zalando.core.ui.view.BaseView;

/**
 * Base class for sub presenter logic that can be injected to presenters to delegate functionality
 * to simplify presenter logic
 */
public abstract class SubPresenter<T extends BaseView> {

  protected final DisposableHelper disposableHelper;
  protected T view;

  protected SubPresenter(DisposableHelper disposableHelper) {
    this.disposableHelper = disposableHelper;
  }

  /**
   * Sets the {@link U} instance to link the {@link SubPresenter} with the UI so that the
   * presenter module can hold same reference to the view.
   *
   * @param view {@link U} to link the {@link SubPresenter} with the UI
   */
  @SuppressWarnings("unchecked")
  // It's good to crash if casting is not accomplished, all views must extend BaseView
  @CallSuper
  public <U extends BaseView> void setView(U view) {
    this.view = (T) view;
  }
}
