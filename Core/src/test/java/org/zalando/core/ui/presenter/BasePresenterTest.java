package org.zalando.core.ui.presenter;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.io.Serializable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.zalando.core.BuildConfig;
import org.zalando.core.domain.helper.DisposableHelper;
import org.zalando.core.ui.view.BaseView;

/**
 * Tests for {@link BasePresenter} class
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
    sdk = Build.VERSION_CODES.LOLLIPOP,
    manifest = "src/main/AndroidManifest.xml")
public class BasePresenterTest {

  private MockBasePresenter basePresenter;

  @Mock
  private BaseView baseView;

  @Before
  public void setUp() {
    basePresenter = new MockBasePresenter(new DisposableHelper());
    baseView = mock(BaseView.class);
  }

  @Test
  public void testHasSubscriptionManager() {

    assertNotNull(basePresenter.getDisposableHelper());
  }

  @Test(expected = IllegalStateException.class)
  public void testSetViewNotCalledBeforeResumeException() {

    basePresenter.resume();
  }

  @Test(expected = IllegalStateException.class)
  public void testSetViewNotCalledBeforeInitialiseException() {

    basePresenter.initialise(new Bundle());
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
    basePresenter.initialise(new Bundle());
    basePresenter.resume();

    // Add Disposable
    basePresenter.getDisposableHelper().addDisposable(Observable.never().subscribe());

    // Check that the disposable exists
    assertTrue(basePresenter.getDisposableHelper().hasDisposables());

    // Pause it
    basePresenter.destroy();

    // Check if undisposed
    assertFalse(basePresenter.getDisposableHelper().hasDisposables());
  }

  @Test
  public void testInitViewCalled() {

    // Set mocked view
    basePresenter.setView(baseView);
    // Init presenter
    basePresenter.initialise(new Bundle());
    basePresenter.resume();

    verify(baseView).initView(any(Bundle.class));
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
    basePresenter.initialise(new Bundle());
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
    basePresenter = new MockBasePresenter(new DisposableHelper());
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
    MockBasePresenter(DisposableHelper disposableHelper) {
      super(disposableHelper);
    }

    @Override
    public void initialise(@NonNull Bundle savedInstanceState) {
      super.initialise(savedInstanceState);
      testSavingInt = savedInstanceState.getInt("savedInt");
      testSavingBoolean = savedInstanceState.getBoolean("savedBool");
      testSavingFloat = savedInstanceState.getFloat("savedFloat");
      testSavingSerializable = savedInstanceState.getSerializable("savedSer");
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
