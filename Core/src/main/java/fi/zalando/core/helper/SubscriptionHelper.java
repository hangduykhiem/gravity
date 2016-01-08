package fi.zalando.core.helper;

import android.support.annotation.NonNull;

import rx.Subscription;

/**
 * Definition of methods to handle unsubscriptions of {@link rx.Observable} when {@link
 * android.app.Activity}s of {@link android.app.Fragment}s are stopped
 *
 * Created by jduran on 19/11/15.
 */
public interface SubscriptionHelper {

    /**
     * Adds a {@link Subscription} to the {@link SubscriptionHelper}
     *
     * @param subscription {@link Subscription} tp add
     */
    public void addSubscription(@NonNull Subscription subscription);

    /**
     * Checks if the manager has any subscription attached to it
     *
     * @return {@link Boolean} indicating if the manager has subscriptions attached
     */
    public boolean hasSubscriptions();

    /**
     * Unsubscribe all the added {@link Subscription}s
     */
    public void unsubscribeAll();

}
