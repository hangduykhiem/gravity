package fi.zalando.core.module;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.data.LocationRepository;
import fi.zalando.core.data.helper.LocationHelper;
import io.realm.RealmObject;

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
     * @param applicationContext {@link Context} of the app
     * @param locationHelper     {@link LocationHelper} to inject
     * @return {@link LocationRepository} initialised
     */
    @Provides
    public LocationRepository provideLocationRepository(Context applicationContext,
                                                        LocationHelper locationHelper) {

        return new LocationRepository(new ReactiveLocationProvider(applicationContext),
                locationHelper);
    }

    /**
     * Provides a GSON instance that is able to work with Realm Objects
     *
     * @return {@link Gson} instance to work with Realm
     */
    @Provides
    @Named("realmDataParser")
    public Gson provideRealmGson() {

        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

}
