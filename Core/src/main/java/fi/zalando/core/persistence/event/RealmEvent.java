package fi.zalando.core.persistence.event;

import android.support.annotation.Nullable;

import fi.zalando.core.utils.EqualUtils;

/**
 * EventBus event class for Realm Events
 *
 * @param <E> {@link E} of the hosted model
 */
public class RealmEvent<E> {

    @Nullable
    private final Object id;

    /**
     * General constructor
     */
    public RealmEvent() {

        id = null;
    }

    /**
     * Constructor to use when the realm is about a model with the given id
     *
     * @param id {@link Object} with the id
     */
    public RealmEvent(@Nullable Object id) {

        this.id = id;
    }

    /**
     * Tells if due to the event a realm update is required
     *
     * @param id {@link Object} with the ID you care about
     * @return {@link Boolean} indicating if observable should reQuery realm
     */
    public boolean requiresUpdate(Object id) {

        return this.id == null || this.id.equals(id);
    }

    /**
     * Provides the ID of the related item that was updated/deleted
     *
     * @return {@link String} with the id
     */
    @Nullable
    public Object getId() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {

        if (other instanceof RealmEvent) {
            RealmEvent<E> otherRealmEvent = (RealmEvent<E>) other;
            return EqualUtils.areEqual(getId(), otherRealmEvent.getId());
        }
        return false;
    }

    @Override
    public String toString() {

        return getClass().getName() + "[" +
                "id = " + id + "]";
    }
}
