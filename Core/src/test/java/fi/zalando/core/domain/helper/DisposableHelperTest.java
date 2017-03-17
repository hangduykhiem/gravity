package fi.zalando.core.domain.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link DisposableHelper} class
 *
 * Created by jduran on 19/11/15.
 */
public class DisposableHelperTest {

  private DisposableHelper disposableHelper;

  @Before
  public void setup() {

    disposableHelper = new DisposableHelper();
  }

  @Test
  public void testSubscriptionAndUnsubscribe() {

    // Check it is empty
    assertFalse(disposableHelper.hasDisposables());
    // Create a sample disposable, ensure it's never ending
    Disposable sampleDisposable = Observable.never().subscribe();
    // Add the disposable
    disposableHelper.addDisposable(sampleDisposable, sampleDisposable);
    // Check if it contains something
    assertTrue(disposableHelper.hasDisposables());
    // Unsubscribe all
    disposableHelper.clear();
    // Check it is empty now
    assertFalse(disposableHelper.hasDisposables());
  }

  @Test
  public void testClear() {

    // Check it is empty
    assertFalse(disposableHelper.hasDisposables());
    // Create a sample Subscription, ensure it's never ending
    Disposable sampleDisposable = Observable.never().subscribe();
    // Add the disposable
    disposableHelper.addDisposable(sampleDisposable);
    // Check if it contains something
    assertTrue(disposableHelper.hasDisposables());
    // Clear all
    disposableHelper.clear();
    // Check it is empty now
    assertFalse(disposableHelper.hasDisposables());
  }

}
