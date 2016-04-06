package fi.zalando.core.module;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.ui.presenter.StubPresenter;

/**
 * Dagger module that provides UI related dependencies
 *
 * Created by jduran on 30/11/15.
 */
@Module
public class BaseUIModule {

    /**
     * Provides a {@link StubPresenter} instance dependency
     *
     * @param subscriptionHelper {@link SubscriptionHelper} to inject
     * @return {@link StubPresenter} instance
     */
    @Provides
    public StubPresenter provideStubPresenter(SubscriptionHelper subscriptionHelper) {

        return new StubPresenter(subscriptionHelper);
    }
}
