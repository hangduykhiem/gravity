package fi.zalando.core.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.persistence.KeyChain;
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
     * Provides a {@link KeyChain} instance dependency
     *
     * @return {@link KeyChain} instance
     */
    @Provides
    @Singleton
    public KeyChain provideKeyChain(PersistentHashTable persistentHashTable) {

        return new KeyChain(persistentHashTable);
    }

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
