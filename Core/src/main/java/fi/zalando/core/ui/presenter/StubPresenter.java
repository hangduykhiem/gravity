package fi.zalando.core.ui.presenter;

import fi.zalando.core.domain.helper.SubscriptionHelper;
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
    public StubPresenter(SubscriptionHelper subscriptionHelper) {

        super(subscriptionHelper);
    }
}
