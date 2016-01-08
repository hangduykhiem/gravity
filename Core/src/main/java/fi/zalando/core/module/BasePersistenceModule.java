package fi.zalando.core.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.helper.SubscriptionHelper;
import fi.zalando.core.persistence.PersistentHashTable;
import fi.zalando.core.persistence.impl.SharedPreferencesPersistentHashTableImpl;

/**
 * Module that provides the implementations of all the injected persistence dependencies
 *
 * Created by jduran on 23/11/15.
 */
@Module
public class BasePersistenceModule {

    /**
     * Provides a {@link SubscriptionHelper} instance dependency
     *
     * @return {@link SubscriptionHelper} instance
     */
    @Provides
    public PersistentHashTable providePersistentHashTable(Context applicationContext) {

        return new SharedPreferencesPersistentHashTableImpl(applicationContext);
    }
}
