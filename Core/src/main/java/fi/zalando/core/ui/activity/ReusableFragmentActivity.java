package fi.zalando.core.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.lang.reflect.Constructor;

import fi.zalando.core.R;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.ui.presenter.StubPresenter;
import fi.zalando.core.ui.view.ReusableFragmentActivityView;
import fi.zalando.core.utils.PlatformUtils;
import timber.log.Timber;

/**
 * An empty, reusable Activity to host Fragments.
 * Created by vraisanen on 14.4.2016.
 */
public class ReusableFragmentActivity extends BaseActivity implements
        ReusableFragmentActivityView {

    /**
     * Set this flag to show toolbar in this Activity.
     */
    public static final int FLAG_TOOLBAR = 1;

    private static final String TAG_FRAGMENT_NAME = "tag.fragment.name";
    private static final String TAG_FRAGMENT_BUNDLE = "tag.fragment.bundle";
    private static final String TAG_ACTIVITY_OPTIONS = "tag.activity.options";
    public static Class activityClass = ReusableFragmentActivity.class;

    StubPresenter stubPresenter;

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it. Toolbar will be enabled.
     *
     * @param launchActivity {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass  Fragment to open inside this new Activity.
     */
    public static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass) {
        launch(launchActivity, fragmentClass, null, FLAG_TOOLBAR);
    }

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it. Toolbar will be enabled.
     *
     * @param launchActivity    {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     */
    @SuppressWarnings("unused")
    public static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass,
                              @Nullable Bundle bundleForFragment) {
        launch(launchActivity, fragmentClass, bundleForFragment, FLAG_TOOLBAR);
    }

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it.
     *
     * @param launchActivity    {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass,
                              @Nullable Bundle bundleForFragment, int optionFlags) {
        if (fragmentClass.isInstance(Fragment.class)) {
            throw new ClassCastException("fragmentClass must extend " + Fragment.class.getName());
        }

        //Pack the Fragment name and Bundle to the Intent:
        Intent reusableFragmentActivityIntent = new Intent(launchActivity, activityClass);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_BUNDLE, bundleForFragment);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_NAME, fragmentClass.getName());
        reusableFragmentActivityIntent.putExtra(TAG_ACTIVITY_OPTIONS, optionFlags);
        //Launch the Activity:
        ActivityCompat.startActivity(launchActivity, reusableFragmentActivityIntent, PlatformUtils
                .isNewerOrEqualSDKVersion(Build.VERSION_CODES.JELLY_BEAN) ? ActivityOptions
                .makeCustomAnimation(launchActivity, R.anim.activity_slidein_left, R.anim
                        .activity_slideout_left).toBundle() : null);
    }

    /**
     * Creates an Intent to launch ReusableFragmentActivity with the given Fragment
     *
     * @param launchActivity    {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Intent createIntent(@NonNull Context launchActivity, @NonNull Class fragmentClass,
                                      @Nullable Bundle bundleForFragment, int optionFlags) {
        if (fragmentClass.isInstance(Fragment.class)) {
            throw new ClassCastException("fragmentClass must extend " + Fragment.class.getName());
        }

        //Pack the Fragment name and Bundle to the Intent:
        Intent reusableFragmentActivityIntent = new Intent(launchActivity, activityClass);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_BUNDLE, bundleForFragment);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_NAME, fragmentClass.getName());
        reusableFragmentActivityIntent.putExtra(TAG_ACTIVITY_OPTIONS, optionFlags);

        return reusableFragmentActivityIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initView() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //Show/hide toolbar:
            if (isFlagSet(FLAG_TOOLBAR)) {
                //Enable back-button in the toolbar:
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().hide();
            }
        }

        //Initialise Fragment:
        //Get Fragment details from the Intent Bundle:
        Bundle bundleForFragment = getIntent().getBundleExtra(TAG_FRAGMENT_BUNDLE);
        String className = getIntent().getStringExtra(TAG_FRAGMENT_NAME);
        //Use reflection to setup the Fragment:
        try {
            //Get the class (Fragment):
            Class<?> fragmentClass = Class.forName(className);
            //Get the default empty constructor:
            Constructor<?> constructor = fragmentClass.getConstructor();
            //Create new instance of the Fragment:
            Fragment fragment = (Fragment) constructor.newInstance();
            //Give the Bundle as argument:
            fragment.setArguments(bundleForFragment);
            //Show the Fragment:
            addInitialFragment(R.id.reusablefragmentactivity_fragmentcontainer,
                    fragment);
        } catch (Exception e) {
            Timber.e(e, "Error when initializing Fragment.");
            throw new IllegalStateException("Error when initializing Fragment.");
        }
    }

    @Override
    protected int getSubActivityLayoutId() {

        return R.layout.activity_reusablefragmentactivity;
    }

    @NonNull
    @Override
    protected StubPresenter getPresenter() {

        return stubPresenter;
    }

    @Override
    @CallSuper
    protected void injectDependencies() {
        stubPresenter = new StubPresenter(new SubscriptionHelper());
    }

    @Override
    public void switchFragment(@NonNull Fragment fragment) {

        super.switchFragment(R.id.reusablefragmentactivity_fragmentcontainer, fragment,
                false, false);
    }

    @Override
    public void switchFragment(Fragment fragment, boolean addToBackStack, boolean animate) {

        super.switchFragment(R.id.reusablefragmentactivity_fragmentcontainer, fragment,
                addToBackStack, animate);
    }

    @Override
    public void setToolbarTitle(String title) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setToolbarTitle(@StringRes int stringResourceId) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(stringResourceId);
        }
    }

    /**
     * Returns true if the given flag has been set.
     * @param flagToCheck flag to check
     * @return true, if the flag was set.
     */
    protected boolean isFlagSet(int flagToCheck) {
        int flags = getIntent().getIntExtra(TAG_ACTIVITY_OPTIONS, FLAG_TOOLBAR);
        return (flags & flagToCheck) == flagToCheck;
    }
}
