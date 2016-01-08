package fi.zalando.core.helper;

import org.junit.Before;
import org.junit.Test;

import fi.zalando.core.module.BaseHelperModule;
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

        subscriptionHelper = new BaseHelperModule().provideSubscriptionHelper();
    }

    @Test
    public void testSubscriptionAndUnsubscribe() {

        // Check it is empty
        assertFalse(subscriptionHelper.hasSubscriptions());

        // Create a sample Subscription, use timer to ensure it's not automatically finishing
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

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testNotNullPreconditions() {

        subscriptionHelper.addSubscription(null);
    }

}
