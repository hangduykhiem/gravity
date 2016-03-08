package fi.zalando.core.module;

import android.content.Context;
import android.location.LocationManager;

import org.greenrobot.eventbus.EventBus;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.data.helper.GooglePlayServicesHelper;
import fi.zalando.core.data.helper.LocationHelper;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.helper.CleaningHelper;

/**
 * Module that provides the implementations of all the injected helpers
 *
 * Created by jduran on 19/11/15.
 */
@Module
public class BaseHelperModule {

    /**
     * Provides a {@link CleaningHelper} instance dependency
     *
     * @param eventBus {@link EventBus} instance
     * @return {@link CleaningHelper} instance
     */
    @Provides
    @Singleton
    public CleaningHelper provideCleaningHelper(EventBus eventBus) {

        return new CleaningHelper(eventBus);
    }

    /**
     * Provides a {@link GooglePlayServicesHelper} instance dependency
     *
     * @param applicationContext {@link Context} of the app
     * @return {@link GooglePlayServicesHelper} instance
     */
    @Provides
    public GooglePlayServicesHelper provideGooglePlayServicesHelper(Context applicationContext) {

        return new GooglePlayServicesHelper(new ReactiveLocationProvider(applicationContext));
    }

    /**
     * Provides a {@link LocationHelper} instance dependency
     *
     * @param applicationContext {@link Context} of the app
     * @param locationManager    {@link LocationManager} to inject
     * @return {@link LocationHelper} instance
     */
    @Provides
    public LocationHelper provideLocationHelper(Context applicationContext,
                                                LocationManager locationManager) {

        return new LocationHelper(applicationContext, locationManager);
    }

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
