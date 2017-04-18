package org.zalando.core.ui.presenter;

import javax.inject.Inject;
import org.zalando.core.domain.helper.DisposableHelper;
import org.zalando.core.ui.view.StubView;

/**
 * Presenter implementation to support simple activities and fragments.
 */
public class StubPresenter extends BasePresenter<StubView> {

  /**
   * Constructor
   */
  @Inject
  public StubPresenter(DisposableHelper disposableHelper) {

    super(disposableHelper);
  }
}
