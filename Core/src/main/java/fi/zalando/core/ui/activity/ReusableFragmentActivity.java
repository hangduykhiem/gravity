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
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.lang.reflect.Constructor;

import fi.zalando.core.R;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.ui.fragment.BaseFragment;
import fi.zalando.core.ui.presenter.StubPresenter;
import fi.zalando.core.ui.view.ReusableFragmentActivityView;
import fi.zalando.core.utils.PlatformUtils;

/**
 * An empty, reusable Activity to host Fragments. Created by vraisanen on 14.4.2016.
 */
public class ReusableFragmentActivity extends BaseActivity implements
        ReusableFragmentActivityView {

    /**
     * Set this flag to show toolbar in this Activity.
     */
    public static final int FLAG_TOOLBAR = 1;

    /**
     * Default request code for activityresult
     */
    public static final int REQUEST_DEFAULT = 1111;

    @VisibleForTesting
    public static final String TAG_FRAGMENT_NAME = "tag.fragment.name";
    @VisibleForTesting
    public static final String TAG_FRAGMENT_BUNDLE = "tag.fragment.bundle";
    @VisibleForTesting
    public static final String TAG_ACTIVITY_OPTIONS = "tag.activity.options";
    public static Class activityClass = ReusableFragmentActivity.class;

    StubPresenter stubPresenter;
    private Bundle fragmentBundle;
    private String className;
    private int optionFlags;

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it. Toolbar will be
     * enabled.
     *
     * @param launchActivity {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass  Fragment to open inside this new Activity.
     */
    public static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass) {
        launch(launchActivity, fragmentClass, null, FLAG_TOOLBAR);
    }

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it. Toolbar will be
     * enabled.
     *
     * @param launchActivity    {@link Activity} that is launching the
     * {@link ReusableFragmentActivity}
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
     * @param launchActivity    {@link Activity} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass,
                              @Nullable Bundle bundleForFragment, int optionFlags) {
        if (fragmentClass.isInstance(BaseFragment.class)) {
            throw new ClassCastException("fragmentClass must extend " + BaseFragment.class
                    .getName());
        }

        //Pack the Fragment name and Bundle to the Intent:
        Intent reusableFragmentActivityIntent = new Intent(launchActivity, activityClass);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_NAME, fragmentClass.getName());
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_BUNDLE, bundleForFragment);
        reusableFragmentActivityIntent.putExtra(TAG_ACTIVITY_OPTIONS, optionFlags);
        //Launch the Activity:
        ActivityCompat.startActivity(launchActivity, reusableFragmentActivityIntent, PlatformUtils
                .isNewerOrEqualSDKVersion(Build.VERSION_CODES.JELLY_BEAN) ? ActivityOptions
                .makeCustomAnimation(launchActivity, R.anim.activity_slidein_left, R.anim
                        .activity_slideout_left).toBundle() : null);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchActivity {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass  Fragment to open inside this new Activity.
     */
    public static void launchForResult(@NonNull Activity launchActivity,
                                       @NonNull Class fragmentClass) {
        launchForResult(launchActivity, fragmentClass, REQUEST_DEFAULT);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchActivity    {@link Activity} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     */
    public static void launchForResult(@NonNull Activity launchActivity,
                                       @NonNull Class fragmentClass,
                                       @Nullable Bundle bundleForFragment) {
        launchForResult(launchActivity, fragmentClass, bundleForFragment, REQUEST_DEFAULT);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchActivity {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass  Fragment to open inside this new Activity.
     * @param requestCode    Request code for the activity result.
     */
    public static void launchForResult(@NonNull Activity launchActivity,
                                       @NonNull Class fragmentClass,
                                       int requestCode) {
        launchForResult(launchActivity, fragmentClass, null, requestCode);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchActivity    {@link Activity} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param requestCode       Request code for the activity result.
     */
    public static void launchForResult(@NonNull Activity launchActivity,
                                       @NonNull Class fragmentClass,
                                       @Nullable Bundle bundleForFragment,
                                       int requestCode) {
        launchForResult(launchActivity, fragmentClass, bundleForFragment, requestCode,
                FLAG_TOOLBAR);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchActivity    {@link Activity} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param requestCode       Request code for the activity result.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void launchForResult(@NonNull Activity launchActivity,
                                       @NonNull Class fragmentClass,
                                       @Nullable Bundle bundleForFragment,
                                       int requestCode,
                                       int optionFlags) {
        //Launch the Activity for result:
        ActivityCompat.startActivityForResult(
                launchActivity,
                createIntent(launchActivity, fragmentClass, bundleForFragment, optionFlags),
                requestCode,
                PlatformUtils.isNewerOrEqualSDKVersion(Build.VERSION_CODES.JELLY_BEAN) ?
                        ActivityOptions.makeCustomAnimation(
                                launchActivity,
                                R.anim.activity_slidein_left,
                                R.anim.activity_slideout_left).toBundle() : null);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchFragment {@link Fragment} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass  Fragment to open inside this new Activity.
     */
    public static void launchFromFragmentForResult(@NonNull Fragment launchFragment,
                                                   @NonNull Class fragmentClass) {
        launchFromFragmentForResult(launchFragment, fragmentClass, REQUEST_DEFAULT);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchFragment    {@link Fragment} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     */
    public static void launchFromFragmentForResult(@NonNull Fragment launchFragment,
                                                   @NonNull Class fragmentClass,
                                                   @Nullable Bundle bundleForFragment) {
        launchFromFragmentForResult(launchFragment, fragmentClass, bundleForFragment,
                REQUEST_DEFAULT);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchFragment {@link Fragment} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass  Fragment to open inside this new Activity.
     * @param requestCode    Request code for the activity result.
     */
    public static void launchFromFragmentForResult(@NonNull Fragment launchFragment,
                                                   @NonNull Class fragmentClass,
                                                   int requestCode) {
        launchFromFragmentForResult(launchFragment, fragmentClass, null, requestCode);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchFragment    {@link Fragment} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param requestCode       Request code for the activity result.
     */
    public static void launchFromFragmentForResult(@NonNull Fragment launchFragment,
                                                   @NonNull Class fragmentClass,
                                                   @Nullable Bundle bundleForFragment,
                                                   int requestCode) {
        launchFromFragmentForResult(launchFragment, fragmentClass, bundleForFragment, requestCode,
                FLAG_TOOLBAR);
    }

    /**
     * Launches ReusableFragmentActivity for result and opens the given Fragment in it.
     *
     * @param launchFragment    {@link Fragment} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param requestCode       Request code for the activity result.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void launchFromFragmentForResult(@NonNull Fragment launchFragment,
                                                   @NonNull Class fragmentClass,
                                                   @Nullable Bundle bundleForFragment,
                                                   int requestCode,
                                                   int optionFlags) {
        //Launch the Activity for result:
        launchFragment.startActivityForResult(
                createIntent(
                        launchFragment.getActivity(),
                        fragmentClass,
                        bundleForFragment,
                        optionFlags),
                requestCode,
                PlatformUtils.isNewerOrEqualSDKVersion(Build.VERSION_CODES.JELLY_BEAN) ?
                        ActivityOptions.makeCustomAnimation(
                                launchFragment.getActivity(),
                                R.anim.activity_slidein_left,
                                R.anim.activity_slideout_left).toBundle() : null);
    }

    /**
     * Creates an Intent to launch ReusableFragmentActivity with the given Fragment
     *
     * @param launchActivity    {@link Activity} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Intent createIntent(@NonNull Context launchActivity, @NonNull Class fragmentClass,
                                      @Nullable Bundle bundleForFragment, int optionFlags) {
        if (fragmentClass.isInstance(BaseFragment.class)) {
            throw new ClassCastException("fragmentClass must extend " + BaseFragment.class
                    .getName());
        }

        //Pack the Fragment name and Bundle to the Intent:
        Intent reusableFragmentActivityIntent = new Intent(launchActivity, activityClass);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_NAME, fragmentClass.getName());
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_BUNDLE, bundleForFragment);
        reusableFragmentActivityIntent.putExtra(TAG_ACTIVITY_OPTIONS, optionFlags);

        return reusableFragmentActivityIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //Get Fragment details from savedInstanceState:
            className = savedInstanceState.getString(TAG_FRAGMENT_NAME);
            fragmentBundle = savedInstanceState.getBundle(TAG_FRAGMENT_BUNDLE);
            optionFlags = savedInstanceState.getInt(TAG_ACTIVITY_OPTIONS, FLAG_TOOLBAR);
        }
        else {
            //Get Fragment details from the Intent:
            className = getIntent().getStringExtra(TAG_FRAGMENT_NAME);
            fragmentBundle = getIntent().getBundleExtra(TAG_FRAGMENT_BUNDLE);
            optionFlags = getIntent().getIntExtra(TAG_ACTIVITY_OPTIONS, FLAG_TOOLBAR);
        }
        //Call onCreate last so that the previous values are initialised:
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG_FRAGMENT_NAME, className);
        outState.putBundle(TAG_FRAGMENT_BUNDLE, fragmentBundle);
        outState.putInt(TAG_ACTIVITY_OPTIONS, optionFlags);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        //Use reflection to setup the Fragment:
        try {
            //Get the class (Fragment):
            Class<?> fragmentClass = Class.forName(className);
            //Get the default empty constructor:
            Constructor<?> constructor = fragmentClass.getConstructor();
            //Create new instance of the Fragment:
            BaseFragment fragment = (BaseFragment) constructor.newInstance();
            //Give the Bundle as argument:
            fragment.setArguments(fragmentBundle);
            //Show the Fragment:
            setFragment(R.id.reusablefragmentactivity_fragmentcontainer, fragment);
        } catch (Exception e) {
            throw new IllegalStateException("Error when initializing Fragment.", e);
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
    public void switchFragment(@NonNull BaseFragment fragment) {

        super.switchFragment(R.id.reusablefragmentactivity_fragmentcontainer, fragment,
                false, false);
    }

    @Override
    public void switchFragment(BaseFragment fragment, boolean addToBackStack, boolean animate) {

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
     *
     * @param flagToCheck flag to check
     * @return true, if the flag was set.
     */
    protected boolean isFlagSet(int flagToCheck) {
        return (optionFlags & flagToCheck) == flagToCheck;
    }
}
