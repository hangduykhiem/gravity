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
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Constructor;

import fi.zalando.core.BuildConfig;
import fi.zalando.core.R;
import fi.zalando.core.domain.helper.SubscriptionHelper;
import fi.zalando.core.ui.fragment.BaseFragment;
import fi.zalando.core.ui.presenter.StubPresenter;
import fi.zalando.core.ui.view.ReusableFragmentActivityView;
import timber.log.Timber;

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
    public static final String TAG_FRAGMENT_NAME =
            BuildConfig.APPLICATION_ID + ".tag.fragment.name";
    @VisibleForTesting
    public static final String TAG_FRAGMENT_BUNDLE =
            BuildConfig.APPLICATION_ID + ".tag.fragment.bundle";
    @VisibleForTesting
    public static final String TAG_ACTIVITY_OPTIONS =
            BuildConfig.APPLICATION_ID + ".tag.activity.options";

    public static Class activityClass = ReusableFragmentActivity.class;

    StubPresenter stubPresenter;
    private Bundle fragmentBundle;
    private String className;
    private int optionFlags;

    private static StringBuilder sb = new StringBuilder();

    /**
     * Launches ReusableFragmentActivity, and opens the given Fragment in it.
     *
     * @param launchActivity    {@link Activity} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @SafeVarargs
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void launch(@NonNull Activity launchActivity, @NonNull Class fragmentClass,
                               @Nullable Bundle bundleForFragment, int optionFlags,
                               Pair<View, String>... sharedElements) {
        if (fragmentClass.isInstance(BaseFragment.class)) {
            throw new ClassCastException("fragmentClass must extend " + BaseFragment.class
                    .getName());
        }

        final Bundle options;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
                && sharedElements != null
                && sharedElements.length > 0) {
            options = ActivityOptions.makeSceneTransitionAnimation(
                    launchActivity,
                    sharedElements).toBundle();
        }
        else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            options = ActivityOptions.makeCustomAnimation(
                    launchActivity,
                    R.anim.activity_slidein_left,
                    R.anim.activity_slideout_left).toBundle();
        }
        else {
            options = null;
        }

        //Logs:
        sb = new StringBuilder(); //Clear the builder
        sb.append("Launching Activity: " + launchActivity.getLocalClassName()
                + " Fragment: " + fragmentClass.getName() + " ");

        //Pack the Fragment name and Bundle to the Intent:
        Intent reusableFragmentActivityIntent = new Intent(launchActivity, activityClass);
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_NAME, fragmentClass.getName());
        reusableFragmentActivityIntent.putExtra(TAG_FRAGMENT_BUNDLE, bundleForFragment);
        reusableFragmentActivityIntent.putExtra(TAG_ACTIVITY_OPTIONS, optionFlags);
        //Launch the Activity:
        ActivityCompat.startActivity(launchActivity, reusableFragmentActivityIntent, options);
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
    @SafeVarargs
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void launchForResult(@NonNull Activity launchActivity,
                                        @NonNull Class fragmentClass,
                                        @Nullable Bundle bundleForFragment,
                                        int requestCode,
                                        int optionFlags,
                                        Pair<View, String>... sharedElements) {
        //Logs:
        sb = new StringBuilder(); //Clear the builder
        sb.append("Launching Activity: " + launchActivity.getLocalClassName()
                + " Fragment: " + fragmentClass.getName() + " ");

        final Bundle options;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
                && sharedElements != null
                && sharedElements.length > 0) {
            options = ActivityOptions.makeSceneTransitionAnimation(
                    launchActivity,
                    sharedElements).toBundle();
        }
        else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            options = ActivityOptions.makeCustomAnimation(
                    launchActivity,
                    R.anim.activity_slidein_left,
                    R.anim.activity_slideout_left).toBundle();
        }
        else {
            options = null;
        }

        //Launch the Activity for result:
        ActivityCompat.startActivityForResult(
                launchActivity,
                createIntent(launchActivity, fragmentClass, bundleForFragment, optionFlags),
                requestCode,
                options);
    }

    /**
     * Launches ReusableFragmentActivity and opens the given Fragment in it, returning the result
     * to the {@code launchFragment}.
     *
     * @param launchFragment    {@link Fragment} that is launching the
     * {@link ReusableFragmentActivity}
     * @param fragmentClass     Fragment to open inside this new Activity.
     * @param bundleForFragment Bundle to be passed on to the Fragment as arguments. Can be null.
     * @param requestCode       Request code for the activity result.
     * @param optionFlags       Options to be passed on and applied to the new Activity.
     */
    @SafeVarargs
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void launchFromFragmentForResult(@NonNull Fragment launchFragment,
                                        @NonNull Class fragmentClass,
                                        @Nullable Bundle bundleForFragment,
                                        int requestCode,
                                        int optionFlags,
                                        Pair<View, String>... sharedElements) {
        //Logs:
        sb = new StringBuilder(); //Clear the builder
        sb.append("Launching Fragment: " + launchFragment.getClass().getSimpleName()
                + " Fragment: " + fragmentClass.getName() + " ");

        final Bundle options;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
                && sharedElements != null
                && sharedElements.length > 0) {
            options = ActivityOptions.makeSceneTransitionAnimation(
                    launchFragment.getActivity(),
                    sharedElements).toBundle();
        }
        else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            options = ActivityOptions.makeCustomAnimation(
                    launchFragment.getActivity(),
                    R.anim.activity_slidein_left,
                    R.anim.activity_slideout_left).toBundle();
        }
        else {
            options = null;
        }

        //Launch the Activity for result:
        launchFragment.startActivityForResult(
                createIntent(launchFragment.getActivity(), fragmentClass,
                        bundleForFragment, optionFlags),
                requestCode,
                options);
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
        sb.append("onCreate["+savedInstanceState+", "+getIntent()+"]");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        sb.append("onResume ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        sb.append("onPause ");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        sb.append("onRestart ");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        sb.append("onStop ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        sb.append("onDestroy ");
        super.onDestroy();
    }

    @Override
    public void initialise(@NonNull Bundle initBundle) {
        sb.append("initialise["+className+", "+fragmentBundle+"] ");
        super.initialise(initBundle);
        //Get Fragment details from initBundle:
        className = initBundle.getString(TAG_FRAGMENT_NAME);
        fragmentBundle = initBundle.getBundle(TAG_FRAGMENT_BUNDLE);
        optionFlags = initBundle.getInt(TAG_ACTIVITY_OPTIONS, FLAG_TOOLBAR);
        sb.append("initialiseParsed["+className+", "+fragmentBundle+"] ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        sb.append("onSave ");
        outState.putString(TAG_FRAGMENT_NAME, className);
        outState.putBundle(TAG_FRAGMENT_BUNDLE, fragmentBundle);
        outState.putInt(TAG_ACTIVITY_OPTIONS, optionFlags);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        sb.append("onRestore ");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void initView(@NonNull Bundle initState) {
        sb.append("initView["+className+", "+fragmentBundle+"] ");
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
            //CRASH!
            //Collect more logs:
            if (getIntent() != null && getIntent().getExtras() != null) {
                Bundle extras = getIntent().getExtras();
                for (String key : extras.keySet()) {
                    sb.append("key: " + key + " value: " + extras.get(key));
                }
            }
            //Send crash log to HockeyApp:
            Timber.wtf(e, "Error when initializing Fragment: " + sb.toString());
            //Recover by closing the Activity:
            finish();
        }
    }

    @Override
    protected int getSubActivityLayoutId() {

        return R.layout.activity_reusablefragmentactivity;
    }

    @NonNull
    @Override
    protected StubPresenter getPresenter() {
        sb.append("getPresenter ");
        return stubPresenter;
    }

    @Override
    @CallSuper
    protected void injectDependencies() {
        sb.append("inject ");
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
        sb.append("switchFragment ");
        super.switchFragment(R.id.reusablefragmentactivity_fragmentcontainer, fragment,
                false, false);
    }

    @Override
    public void switchFragment(BaseFragment fragment, boolean addToBackStack, boolean animate) {
        sb.append("switchFragment ");
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
    protected void setFragment(int fragmentContainerId, BaseFragment fragment) {
        sb.append("setFragment ");
        super.setFragment(fragmentContainerId, fragment);
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

    /**
     * Class to help launching {@link ReusableFragmentActivity}s
     */
    public static class Builder {
        private Activity launchActivity;
        private Fragment launchFragment;
        private Class fragmentClass;
        private Bundle bundleForFragment;
        private int optionFlags = FLAG_TOOLBAR;
        private int requestCode = REQUEST_DEFAULT;
        private Pair<View, String>[] sharedElements;

        /**
         * Starts building the {@link ReusableFragmentActivity} from scratch
         * @param launchActivity {@link Activity} to launch the {@link ReusableFragmentActivity}
         *                                       from
         * @param fragmentClass {@link Class} of the {@link BaseFragment} to launch in the Activity
         */
        public Builder(@NonNull Activity launchActivity, @NonNull Class fragmentClass) {
            this.launchActivity = launchActivity;
            this.fragmentClass = fragmentClass;
        }

        /**
         * Starts building the {@link ReusableFragmentActivity} from scratch
         * @param launchFragment {@link Fragment} to launch the {@link ReusableFragmentActivity}
         *                                       from
         * @param fragmentClass {@link Class} of the {@link BaseFragment} to launch in the Activity
         */
        public Builder(@NonNull Fragment launchFragment, @NonNull Class fragmentClass) {
            this.launchFragment = launchFragment;
            this.fragmentClass = fragmentClass;
        }

        /**
         * Sets the {@link Bundle} to pass along to the fragment
         * @param bundleForFragment {@link Bundle}
         * @return this
         */
        public Builder setBundleForFragment(Bundle bundleForFragment) {
            this.bundleForFragment = bundleForFragment;
            return this;
        }

        /**
         * Sets the option flags for the new Activity, such as {@code FLAG_TOOLBAR}
         * @param optionFlags options to be applied in the new Activity
         * @return this
         */
        public Builder setOptionFlags(int optionFlags) {
            this.optionFlags = optionFlags;
            return this;
        }

        /**
         * Sets the request code to be used when launching the Activity for result
         * @param requestCode for launchActivityForResult
         * @return this
         */
        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        /**
         * Sets shared elements for animation purposes
         * @param sharedElements array of {@link View} and {@link String} {@link Pair}s
         * @return this
         */
        public Builder setSharedElements(Pair<View, String>[] sharedElements) {
            this.sharedElements = sharedElements;
            return this;
        }

        /**
         * Launches the Activity with the applied settings
         */
        public void launch() {
            if (launchActivity == null) {
                launchActivity = launchFragment.getActivity();
            }
            ReusableFragmentActivity.launch(launchActivity, fragmentClass,
                    bundleForFragment, optionFlags, sharedElements);
        }

        /**
         * Launches the Activity for result with the applied settings. If the
         * {@code launchFragment} is set, the result will be returned to it. Otherwise the
         * result is directed to the Activity.
         */
        public void launchForResult() {
            if (launchActivity == null) {
                ReusableFragmentActivity.launchFromFragmentForResult(launchFragment, fragmentClass,
                        bundleForFragment, requestCode, optionFlags, sharedElements);
            }
            else {
                ReusableFragmentActivity.launchForResult(launchActivity, fragmentClass,
                        bundleForFragment, requestCode, optionFlags, sharedElements);
            }
        }
    }
}
