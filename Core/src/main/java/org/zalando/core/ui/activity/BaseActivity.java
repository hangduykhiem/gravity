package org.zalando.core.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import dagger.Lazy;
import javax.inject.Inject;
import org.zalando.core.BuildConfig;
import org.zalando.core.R;
import org.zalando.core.ui.Navigator;
import org.zalando.core.ui.callback.OnReadyForTransitionListener;
import org.zalando.core.ui.fragment.BaseFragment;
import org.zalando.core.ui.presenter.BasePresenter;
import org.zalando.core.ui.view.BaseView;
import timber.log.Timber;

/**
 * Abstract activity that holds common methods usable by all the {@link android.app.Activity} on the
 * app. It extends {@link AppCompatActivity} to ensure the usage of UI compatibility library.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

  public static final String HAS_SHARED_ELEMENTS = BuildConfig.APPLICATION_ID + ".shared.elements";

  /**
   * Internal private objects
   */
  private FragmentManager fragmentManager;
  private int defaultFragmentLayoutId;
  private OnReadyForTransitionListener onReadyForTransitionListener;

  /**
   * Protected objects
   */
  @Inject
  protected Lazy<Navigator> navigator;

  /**
   * Lifecycle method
   *
   * @param savedInstanceState Null if activity started, with content if restored
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    final Bundle initBundle;
    if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
      initBundle = savedInstanceState;
    } else if (getIntent().getExtras() != null) {
      initBundle = getIntent().getExtras();
    } else {
      initBundle = new Bundle();
    }
    // Call this in case Activities want to do something right after super.onCreate
    initialise(initBundle);
    // Init fragment manager to have an easy access to fragment related operations
    fragmentManager = getSupportFragmentManager();
    // Force injection of dependencies
    injectDependencies();
    // Init content view (if set)
    if (getSubActivityLayoutId() != -1) {
      setContentView(getSubActivityLayoutId());
    }
    // Force ButterKnife to bind all the view and resources
    ButterKnife.bind(this);
    // Set the view to the presenter
    getPresenter().setView(this);
    // Call this in case sub Activities want to do something after onCreate but before initView
    prePresenterInitialise();
    // Init Presenter
    getPresenter().initialise(initBundle);
  }

  /**
   * Called right after super.onCreate(), before injections and setContentView. This can be used
   * to initialise objects before the Views or the presenters are created.
   *
   * @param initBundle if the savedInstanceState received from onCreate is null, this will be the
   * Intent's Extras.
   */
  @CallSuper
  public void initialise(@NonNull Bundle initBundle) {
  }

  /**
   * Lifecycle method
   */
  @Override
  protected void onResume() {

    super.onResume();
    getPresenter().resume();
  }

  /**
   * Lifecycle method
   */
  @Override
  protected void onDestroy() {

    getPresenter().destroy();
    super.onDestroy();
  }

  /**
   * Lifecycle method
   */
  @SuppressWarnings("RestrictedApi")
  @Override
  public void onBackPressed() {
    //Swallow state exceptions caused by fragments in this case:
    try {
      if (fragmentManager.getBackStackEntryCount() > 0) {
        FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(
            fragmentManager.getBackStackEntryCount() - 1);
        final Fragment fragment = fragmentManager
            .findFragmentByTag(backEntry.getName());
        if (fragment != null && fragment instanceof BaseFragment) {
          ((BaseFragment) fragment).onBackStackPop();
        }
        fragmentManager.popBackStack();
      } else if (!isFinishing()) {
        if (fragmentManager.getFragments() != null) {
          final Fragment fragment = fragmentManager.getFragments().get(0);
          if (fragment != null && fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onBackStackPop();
          }
        }

        // Check if the activity has a parent defined in the manifest so we need to rebuild the
        // activity stack
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (upIntent != null
            // Create stack if activity is root and developer
            // has defined parent activity in manifest
            && (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot())) {
          TaskStackBuilder.create(this)
              .addNextIntentWithParentStack(upIntent)
              .startActivities();
        }

        // Delegate to android all the back pressed logic
        super.onBackPressed();
      }
    } catch (IllegalStateException exception) {
      Timber.wtf(exception, "Caught exception in onBackPressed");
    }
  }

  /**
   * Lifecycle method
   *
   * @param outState {@link Bundle} where the state of the activity is stored
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {

    getPresenter().onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  /**
   * Called right before Presenter's initialization, but after super.onCreate.
   */
  public void prePresenterInitialise() {
  }

  /**
   * Replaces the fragment to the default fragment container
   *
   * @param fragmentContainerId layout id to place the fragment
   * @param fragment Fragment to add
   * @return {@link Boolean} indicating if the initial fragment was added
   */
  @CallSuper
  protected boolean setInitialFragment(int fragmentContainerId, BaseFragment fragment) {

    defaultFragmentLayoutId = fragmentContainerId;
    if (getInitialFragment() == null) {
      FragmentTransaction ft = fragmentManager.beginTransaction();
      ft.replace(fragmentContainerId, fragment, ((Object) fragment).getClass()
          .getSimpleName());
      ft.commitAllowingStateLoss();
      fragmentManager.executePendingTransactions();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Replaces the fragment to the default fragment container
   *
   * @param fragmentContainerId layout id to place the fragment
   * @param fragment Fragment to add
   */
  @CallSuper
  protected void replaceFragment(int fragmentContainerId, BaseFragment fragment) {
    defaultFragmentLayoutId = fragmentContainerId;
    FragmentTransaction ft = fragmentManager.beginTransaction();
    ft.replace(fragmentContainerId, fragment, ((Object) fragment).getClass()
        .getSimpleName());
    ft.commitAllowingStateLoss();
    fragmentManager.executePendingTransactions();
  }

  /**
   * Provides the Fragment that was added as initial Fragment in the activity. It is useful when
   * restarting activity, to avoid setting fragment again so recovering fragment state would be
   * lost.
   *
   * @return {@link Fragment} that was initially added to the Activity. Null if still nothing has
   * been added
   */
  @Nullable
  protected final BaseFragment getInitialFragment() {
    return fragmentManager.getFragments() != null && fragmentManager.getFragments().size() > 0
        ? (BaseFragment) fragmentManager.getFragments().get(0) : null;
  }

  /**
   * Set the {@link OnReadyForTransitionListener} to listen for the event when the transition
   * is ready.
   *
   * @param onReadyForTransitionListener {@link OnReadyForTransitionListener}
   */
  public void setOnReadyForTransitionListener(OnReadyForTransitionListener
      onReadyForTransitionListener) {
    this.onReadyForTransitionListener = onReadyForTransitionListener;
  }

  /**
   * @return {@link OnReadyForTransitionListener} if one is set
   */
  @Nullable
  public OnReadyForTransitionListener getOnReadyForTransitionListener() {
    return onReadyForTransitionListener;
  }

  /**
   * @return true, if this {@link Activity} was launched with shared elements animation
   */
  public boolean hasSharedElements() {
    return getIntent().getBooleanExtra(HAS_SHARED_ELEMENTS, false);
  }

  /**
   * Abstract method that provides which is the content view of the activity
   *
   * @return Layout content view resource id, or -1 to set no content at all.
   */
  @LayoutRes
  protected abstract int getSubActivityLayoutId();

  /**
   * Provides the {@link BasePresenter} that controls the Activity
   *
   * @return {@link BasePresenter} responsible of controlling the Activity
   */
  @NonNull
  protected abstract BasePresenter getPresenter();

  /**
   * Force subclasses to inject dependencies accordingly
   */
  protected abstract void injectDependencies();

  /**
   * Replaces the fragment container with the given fragment
   *
   * @param newFragment {@link BaseFragment} that will replace the previous one
   * @param fragmentContainerId layout id to place the fragment
   * @param addToBackStack True if wanted to add to BackStack false otherwise
   * @param animate True if you want to animate the change
   */
  @CallSuper
  protected void switchFragment(int fragmentContainerId, BaseFragment newFragment,
      boolean addToBackStack, boolean animate) {

    defaultFragmentLayoutId = fragmentContainerId;
    String tag = ((Object) newFragment).getClass().getSimpleName();
    // check if the fragment does not exist to add it now to the fragment container
    if (fragmentManager.findFragmentByTag(tag) == null) {

      final FragmentTransaction ft = fragmentManager.beginTransaction();

      if (animate) {
        if (addToBackStack) {
          ft.setCustomAnimations(R.anim.fragment_slidein_left, R.anim
                  .fragment_slideout_left,
              R.anim.fragment_slidein_right, R.anim.fragment_slideout_right);
        } else {
          ft.setCustomAnimations(R.anim.fragment_alphain, R.anim.fragment_alphaout);
        }
      }

      ft.replace(fragmentContainerId, newFragment, ((Object) newFragment).getClass()
          .getSimpleName());
      if (addToBackStack) {
        ft.addToBackStack(((Object) newFragment).getClass().getSimpleName());
      }
      ft.commitAllowingStateLoss();
      fragmentManager.executePendingTransactions();
    }
  }

  /**
   * Replaces the fragment container with the given fragment
   *
   * @param newFragment {@link Fragment} that will replace the previous one
   * @param fragmentContainerId layout id to place the fragment
   * @param addToBackStack True if wanted to add to BackStack false otherwise
   * @param enterAnimation Entry animation resource id
   * @param exitAnimation Exit animation resource id
   */
  @CallSuper
  protected void switchFragment(int fragmentContainerId, BaseFragment newFragment, boolean
      addToBackStack, @AnimRes int enterAnimation, @AnimRes int exitAnimation) {

    defaultFragmentLayoutId = fragmentContainerId;
    String tag = ((Object) newFragment).getClass().getSimpleName();
    // check if the fragment does not exist to add it now to the fragment container
    if (fragmentManager.findFragmentByTag(tag) == null) {

      final FragmentTransaction ft = fragmentManager.beginTransaction();

      if (addToBackStack) {
        ft.setCustomAnimations(enterAnimation, exitAnimation, enterAnimation,
            exitAnimation);
      } else {
        ft.setCustomAnimations(enterAnimation, exitAnimation);
      }

      ft.replace(fragmentContainerId, newFragment, ((Object) newFragment).getClass()
          .getSimpleName());
      if (addToBackStack) {
        ft.addToBackStack(((Object) newFragment).getClass().getSimpleName());
      }
      ft.commitAllowingStateLoss();
      fragmentManager.executePendingTransactions();
    }
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.activity_slidein_right, R.anim.activity_slideout_right);
  }
}
