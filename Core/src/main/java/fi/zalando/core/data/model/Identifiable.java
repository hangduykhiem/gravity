package fi.zalando.core.data.model;

import android.support.annotation.Nullable;

/**
 * Interface to define methods of a model that contains a primary and unique ID.
 *
 * Created by jduran on 25/02/16.
 */
public interface Identifiable {

    /**
     * Provides the {@link Object} with the string key of the model. Null if doesn't have an id.
     *
     * @return {@link String} with the primary key. Null if it does not contain it.
     */
    @Nullable
    public String getId();

}
