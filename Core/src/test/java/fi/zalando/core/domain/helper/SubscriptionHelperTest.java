package fi.zalando.core.domain.helper;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Subscription;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SubscriptionHelper} class
 *
 * Created by jduran on 19/11/15.
 */
public class SubscriptionHelperTest {

    private SubscriptionHelper subscriptionHelper;

    @Before
    public void setup() {

        subscriptionHelper = new SubscriptionHelper();
    }

    @Test
    public void testSubscriptionAndUnsubscribe() {

        // Check it is empty
        assertFalse(subscriptionHelper.hasSubscriptions());
        // Create a sample Subscription, ensure it's never ending
        Subscription sampleSubscription = Observable.never().subscribe();
        // Add the subscription
        subscriptionHelper.addSubscription(sampleSubscription);
        // Check if it contains something
        assertTrue(subscriptionHelper.hasSubscriptions());
        // Unsubscribe all
        subscriptionHelper.unsubscribeAll();
        // Check it is empty now
        assertFalse(subscriptionHelper.hasSubscriptions());
    }

    @Test
    public void testClear() {

        // Check it is empty
        assertFalse(subscriptionHelper.hasSubscriptions());
        // Create a sample Subscription, ensure it's never ending
        Subscription sampleSubscription = Observable.never().subscribe();
        // Add the subscription
        subscriptionHelper.addSubscription(sampleSubscription);
        // Check if it contains something
        assertTrue(subscriptionHelper.hasSubscriptions());
        // Clear all
        subscriptionHelper.clear();
        // Check it is empty now
        assertFalse(subscriptionHelper.hasSubscriptions());
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testNotNullPreconditions() {

        subscriptionHelper.addSubscription(null);
    }

}
