package fi.zalando.core.ui.presenter;

import javax.inject.Inject;

import fi.zalando.core.domain.helper.DisposableHelper;
import fi.zalando.core.ui.view.StubView;

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
