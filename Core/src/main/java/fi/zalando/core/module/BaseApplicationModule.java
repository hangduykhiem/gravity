/*
 * Copyright (C) 2015 Jorge Castillo Pérez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.zalando.core.module;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module used to inject application context or generic dependencies.
 *
 * Created by jduran on 30/11/15.
 */
@Module
public class BaseApplicationModule {

    private final Application application;

    /**
     * Constructor
     *
     * @param application Application instance
     */
    public BaseApplicationModule(Application application) {

        this.application = application;
    }

    /**
     * Provides the {@link Application} instance for dependency injections
     *
     * @return {@link Application} instance
     */
    @Provides
    @Singleton
    public Application provideApplication() {

        return application;
    }

    /**
     * Provides the {@link Context} of the app for dependency injections
     *
     * @return {@link Context} of the app
     */
    @Provides
    @Singleton
    public Context provideApplicationContext() {

        return application.getApplicationContext();
    }

    /**
     * Provides the {@link LocationManager} of the app for dependency injections
     *
     * @return {@link LocationManager} of the app
     */
    @Provides
    @Singleton
    public LocationManager provideLocationManager() {

        return (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

}
