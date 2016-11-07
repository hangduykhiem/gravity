package fi.zalando.core.ui.presenter;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.Serializable;

import fi.zalando.core.BuildConfig;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.ui.view.BaseView;
import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link BasePresenter} class
 *
 * Created by jduran on 29/11/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
        "src/main/AndroidManifest.xml")
public class BasePresenterTest {

    private MockBasePresenter basePresenter;

    @Mock
    private BaseView baseView;

    @Before
    public void setUp() {
        basePresenter = new MockBasePresenter(new SubscriptionHelper());
        baseView = mock(BaseView.class);
    }

    @Test
    public void testHasSubscriptionManager() {

        assertNotNull(basePresenter.getSubscriptionHelper());
    }

    @Test(expected = IllegalStateException.class)
    public void testSetViewNotCalledBeforeResumeException() {

        basePresenter.resume();
    }

    @Test(expected = IllegalStateException.class)
    public void testSetViewNotCalledBeforeInitialiseException() {

        basePresenter.initialise(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testInitialisedNotCalledBeforeResumeException() {

        basePresenter.resume();
    }

    @Test
    public void testUnsubscribeOnDestroy() {

        // Set mocked view
        basePresenter.setView(baseView);
        // Init presenter
        basePresenter.initialise(null);
        basePresenter.resume();

        // Add Subscription
        basePresenter.getSubscriptionHelper().addSubscription(Observable.never().subscribe());

        // Check that the subscription exists
        assertTrue(basePresenter.getSubscriptionHelper().hasSubscriptions());

        // Pause it
        basePresenter.destroy();

        // Check if unsubscribed
        assertFalse(basePresenter.getSubscriptionHelper().hasSubscriptions());
    }

    @Test
    public void testInitViewCalled() {

        // Set mocked view
        basePresenter.setView(baseView);
        // Init presenter
        basePresenter.initialise(null);
        basePresenter.resume();

        verify(baseView, times(1)).initView(any());
    }

    @SuppressWarnings("all")
    @Test
    public void testSaveState() {

        Integer mockedIntValue = 2;
        Boolean mockedBooleanValue = true;
        Float mockedFloatValue = 1.1f;
        Serializable mockedSerializable = new String("SerializableExample");

        // Set mocked view
        basePresenter.setView(baseView);
        // Init presenter
        basePresenter.initialise(null);
        basePresenter.resume();
        // Change mocked values
        basePresenter.testSavingInt = mockedIntValue;
        basePresenter.testSavingBoolean = mockedBooleanValue;
        basePresenter.testSavingFloat = mockedFloatValue;
        basePresenter.testSavingSerializable = mockedSerializable;
        // Force save instance state
        // Create bundle to save the state
        Bundle bundle = new Bundle();
        // Verify Bundle is empty
        assertTrue(bundle.isEmpty());
        // Force saving the state
        basePresenter.onSaveInstanceState(bundle);
        // Verify bundle is not empty
        assertFalse(bundle.isEmpty());
        // Destroy the saved variables in the MockBasePresenter and init it again with the bundle
        basePresenter = new MockBasePresenter(new SubscriptionHelper());
        basePresenter.setView(baseView);
        basePresenter.initialise(bundle);
        basePresenter.resume();
        // Check that it was properly restored
        assertEquals(basePresenter.testSavingInt, mockedIntValue);
        assertEquals(basePresenter.testSavingBoolean, mockedBooleanValue);
        assertEquals(basePresenter.testSavingFloat, mockedFloatValue);
        assertEquals(basePresenter.testSavingSerializable, mockedSerializable);
    }

    protected class MockBasePresenter extends BasePresenter<BaseView> {

        Integer testSavingInt;
        Boolean testSavingBoolean;
        Float testSavingFloat;
        Serializable testSavingSerializable;

        /**
         * Constructor
         */
        public MockBasePresenter(SubscriptionHelper subscriptionHelper) {
            super(subscriptionHelper);
        }

        @Override
        public void initialise(@Nullable Bundle savedInstanceState) {
            super.initialise(savedInstanceState);
            if (savedInstanceState != null) {
                testSavingInt = savedInstanceState.getInt("savedInt");
                testSavingBoolean = savedInstanceState.getBoolean("savedBool");
                testSavingFloat = savedInstanceState.getFloat("savedFloat");
                testSavingSerializable = savedInstanceState.getSerializable("savedSer");
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putInt("savedInt", testSavingInt);
            outState.putBoolean("savedBool", testSavingBoolean);
            outState.putFloat("savedFloat", testSavingFloat);
            outState.putSerializable("savedSer", testSavingSerializable);
            super.onSaveInstanceState(outState);
        }
    }
}
