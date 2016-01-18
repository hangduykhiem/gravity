package fi.zalando.core.module;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.domain.helper.SubscriptionHelper;

/**
 * Module that provides the implementations of all the injected helpers
 *
 * Created by jduran on 19/11/15.
 */
@Module
public class BaseHelperModule {

    /**
     * Provides a {@link SubscriptionHelper} instance dependency
     *
     * @return {@link SubscriptionHelper} instance
     */
    @Provides
    public SubscriptionHelper provideSubscriptionHelper() {

        return new SubscriptionHelper();
    }

}
