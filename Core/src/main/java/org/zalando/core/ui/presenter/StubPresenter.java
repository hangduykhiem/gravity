package org.zalando.core.ui.presenter;

import org.zalando.core.domain.helper.DisposableHelper;
import org.zalando.core.ui.view.StubView;
import javax.inject.Inject;

/**
 * Presenter implementation to support simple activities and fragments.
 *
 * Created by vraisanen on 03/03/16.
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