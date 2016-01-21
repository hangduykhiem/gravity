package fi.zalando.core.module;

import android.content.Context;
import android.location.LocationManager;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.data.LocationRepository;
import fi.zalando.core.data.helper.GooglePlayServicesHelper;
import fi.zalando.core.data.helper.LocationHelper;

/**
 * Dagger module that provides data layer related dependencies
 *
 * Created by jduran on 03/12/15.
 */
@Module
public class BaseDataModule {

    /**
     * Provides a {@link LocationRepository} instance dependency
     *
     * @param applicationContext       {@link Context} of the app
     * @param googlePlayServicesHelper {@link GooglePlayServicesHelper} to inject
     * @param locationHelper           {@link LocationHelper} to inject
     * @return {@link LocationRepository} initialised
     */
    @Provides
    public LocationRepository provideLocationRepository(Context applicationContext,
                                                        GooglePlayServicesHelper
                                                                googlePlayServicesHelper,
                                                        LocationHelper locationHelper) {

        return new LocationRepository(applicationContext, googlePlayServicesHelper, locationHelper);
    }

}
