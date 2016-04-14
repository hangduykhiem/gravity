package fi.zalando.core.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import java.lang.reflect.Constructor;

import fi.zalando.core.R;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.ui.presenter.StubPresenter;
import fi.zalando.core.ui.view.ReusableFragmentActivityView;
import fi.zalando.core.utils.PlatformUtils;
import timber.log.Timber;

/*
 * Created by vraisanen on 14.4.2016.
 */
public class ReusableFragmentActivity extends BaseActivity implements
        ReusableFragmentActivityView {

    private static final String TAG_FRAGMENT_NAME = "tag.fragment.name";
    private static final String TAG_FRAGMENT_BUNDLE = "tag.fragment.bundle";
    private static final String TAG_SHOW_TOOLBAR = "tag.toolbar.enable";

    StubPresenter stubPresenter;

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it. Toolbar will be enabled.
     *
     * @param launchActivity {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass  Fragment to open inside this new Activity.
     */
    public static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass) {
        launch(launchActivity, fragmentClass, null, true);
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
        launch(launchActivity, fragmentClass, bundleForFragment, true);
    }

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it.
     *
     * @param launchActivity    {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param showToolbar       Whether or not the toolbar should be enabled inside this Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass,
                              @Nullable Bundle bundleForFragment, boolean showToolbar) {
        if (fragmentClass.isInstance(Fragment.class)) {
            throw new ClassCastException("fragmentClass must extend " + Fragment.class.getName());
        }

        //Pack the Fragment name and Bundle to the Intent:
        Intent reusableFragmentActivityIntent = new Intent(launchActivity, ReusableFragmentActivity
                .class);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_BUNDLE, bundleForFragment);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_NAME, fragmentClass.getName());
        reusableFragmentActivityIntent.putExtra(TAG_SHOW_TOOLBAR, showToolbar);
        //Launch the Activity:
        ActivityCompat.startActivity(launchActivity, reusableFragmentActivityIntent, PlatformUtils
                .isNewerOrEqualSDKVersion(Build.VERSION_CODES.JELLY_BEAN) ? ActivityOptions
                .makeCustomAnimation(launchActivity, R.anim.fragment_slidein_left, R.anim
                        .fragment_slideout_left).toBundle() : null);
    }

    /**
     * Creates an Intent to launch ReusableFragmentActivity with the given Fragment
     *
     * @param launchActivity    {@link Activity} that is launching the {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param showToolbar       Whether or not the toolbar should be enabled inside this Activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Intent createIntent(@NonNull Context launchActivity, @NonNull Class fragmentClass,
                                      @Nullable Bundle bundleForFragment, boolean showToolbar) {
        if (fragmentClass.isInstance(Fragment.class)) {
            throw new ClassCastException("fragmentClass must extend " + Fragment.class.getName());
        }

        //Pack the Fragment name and Bundle to the Intent:
        Intent reusableFragmentActivityIntent = new Intent(launchActivity, ReusableFragmentActivity
                .class);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_BUNDLE, bundleForFragment);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_NAME, fragmentClass.getName());
        reusableFragmentActivityIntent.putExtra(TAG_SHOW_TOOLBAR, showToolbar);

        return reusableFragmentActivityIntent;
    }

    @Override
    public void initView() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //Show/hide toolbar:
            if (getIntent().getBooleanExtra(TAG_SHOW_TOOLBAR, true)) {
                //Enable back-button in the toolbar:
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (toolbar != null) {
                    toolbar.setNavigationOnClickListener(v -> {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
                        } else {
                            finish();
                        }
                    });
                }
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

}
