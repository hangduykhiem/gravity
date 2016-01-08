package fi.zalando.core.helper.impl;

import android.support.annotation.NonNull;

import fi.zalando.core.helper.SubscriptionHelper;
import fi.zalando.core.utils.Preconditions;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Implementation class of {@link SubscriptionHelper}
 *
 * Created by jduran on 19/11/15.
 */
public class SubscriptionHelperImpl implements SubscriptionHelper {

    private CompositeSubscription subscriptions;

    /**
     * Constructor
     */
    public SubscriptionHelperImpl() {

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void addSubscription(@NonNull Subscription subscription) {

        // Check preconditions
        Preconditions.checkNotNull(subscription);

        Timber.d("addSubscription " + subscription.toString());

        subscriptions.add(subscription);
    }

    @Override
    public boolean hasSubscriptions() {

        return subscriptions.hasSubscriptions();
    }

    @Override
    public void unsubscribeAll() {

        Timber.d("unsubscribeAll");

        // Unsubscribe first
        subscriptions.unsubscribe();
        // Force clear of subscriptions
        subscriptions.clear();
    }
}
