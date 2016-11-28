package fi.zalando.core.ui.activity;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.Lazy;
import fi.zalando.core.R;
import fi.zalando.core.ui.Navigator;
import fi.zalando.core.ui.fragment.BaseFragment;
import fi.zalando.core.ui.presenter.BasePresenter;
import fi.zalando.core.ui.view.BaseView;

/**
 * Abstract activity that holds common methods usable by all the {@link android.app.Activity} on the
 * app. It extends {@link AppCompatActivity} to ensure the usage of UI compatibility library.
 *
 * Created by jduran on 17/11/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    /**
     * Internal private objects
     */
    private FragmentManager fragmentManager;

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
        }
        else {
            initBundle = new Bundle();
        }
        // Call this in case Activities want to do something right after super.onCreate
        initialise(initBundle);
        // Init fragment manager to have an easy access to fragment related operations
        fragmentManager = getSupportFragmentManager();
        // Force injection of dependencies
        injectDependencies();
        // Init content view
        setContentView(getSubActivityLayoutId());
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
     * @param initBundle if the savedInstanceState received from onCreate is null, this will be
     *                   the Intent's Extras.
     */
    @CallSuper
    public void initialise(@NonNull Bundle initBundle) {}

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

        super.onDestroy();
        getPresenter().destroy();
    }

    /**
     * Lifecycle method
     */
    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
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
     * @param fragment            Fragment to add
     */
    @CallSuper
    protected void setFragment(int fragmentContainerId, BaseFragment fragment) {

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(fragmentContainerId, fragment, ((Object) fragment).getClass()
                .getSimpleName());
        ft.commit();
    }

    /**
     * Abstract method that provides which is the content view of the activity
     *
     * @return Layout content view resource id
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
     * @param newFragment         {@link BaseFragment} that will replace the previous one
     * @param fragmentContainerId layout id to place the fragment
     * @param addToBackStack      True if wanted to add to BackStack false otherwise
     * @param animate             True if you want to animate the change
     */
    @CallSuper
    protected void switchFragment(int fragmentContainerId, BaseFragment newFragment, boolean
            addToBackStack, boolean animate) {

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
            ft.commit();
        }
    }

    /**
     * Replaces the fragment container with the given fragment
     *
     * @param newFragment         {@link Fragment} that will replace the previous one
     * @param fragmentContainerId layout id to place the fragment
     * @param addToBackStack      True if wanted to add to BackStack false otherwise
     * @param enterAnimation      Entry animation resource id
     * @param exitAnimation       Exit animation resource id
     */
    @CallSuper
    protected void switchFragment(int fragmentContainerId, BaseFragment newFragment, boolean
            addToBackStack, @AnimRes int enterAnimation, @AnimRes int exitAnimation) {

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
            ft.commit();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slidein_right, R.anim.activity_slideout_right);
    }
}
