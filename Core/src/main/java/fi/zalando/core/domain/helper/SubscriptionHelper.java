package fi.zalando.core.domain.helper;

import android.support.annotation.NonNull;

import fi.zalando.core.utils.Preconditions;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Definition of methods to handle unsubscriptions of {@link rx.Observable} when {@link
 * android.app.Activity}s of {@link android.app.Fragment}s are stopped
 *
 * Created by jduran on 19/11/15.
 */
public class SubscriptionHelper {

    private CompositeSubscription subscriptions;

    /**
     * Constructor
     */
    public SubscriptionHelper() {

        subscriptions = new CompositeSubscription();
    }

    /**
     * Adds a {@link Subscription} to the {@link SubscriptionHelper}
     *
     * @param subscription {@link Subscription} tp add
     */
    public void addSubscription(@NonNull Subscription subscription) {

        // Check preconditions
        Preconditions.checkNotNull(subscription);

        Timber.d("addSubscription " + subscription.toString());

        subscriptions.add(subscription);
    }

    /**
     * Clears all the subscriptions hosted in the helper
     */
    public void clear() {

        subscriptions.clear();
    }

    /**
     * Checks if the manager has any subscription attached to it
     *
     * @return {@link Boolean} indicating if the manager has subscriptions attached
     */
    public boolean hasSubscriptions() {

        return subscriptions.hasSubscriptions();
    }

    /**
     * Unsubscribe all the added {@link Subscription}s
     */
    public void unsubscribeAll() {

        Timber.d("unsubscribeAll");

        // Unsubscribe first
        subscriptions.unsubscribe();
        // Force clear of subscriptions
        subscriptions.clear();
    }
}
