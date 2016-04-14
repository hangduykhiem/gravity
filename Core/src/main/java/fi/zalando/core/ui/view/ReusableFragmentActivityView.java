package fi.zalando.core.ui.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

/*
 * Created by vraisanen on 14.4.2016.
 */
public interface ReusableFragmentActivityView extends BaseView {

    /**
     * Replaces the fragment container with the given {@link Fragment} using no animations or back
     * stack.
     *
     * @param fragment {@link Fragment} to switch to.
     */
    public void switchFragment(@NonNull Fragment fragment);

    /**
     * Replaces the fragment container with the given fragment
     *
     * @param fragment       {@link Fragment} that will replace the previous one
     * @param addToBackStack True if wanted to add to BackStack false otherwise
     * @param animate        True if you want to animate the change
     */
    public void switchFragment(Fragment fragment, boolean addToBackStack, boolean animate);

    /**
     * Sets the toolbar title to the given String.
     *
     * @param title The title to use
     */
    public void setToolbarTitle(String title);

    /**
     * Sets the toolbar title to the given String.
     *
     * @param stringResourceId The title to use
     */
    public void setToolbarTitle(@StringRes int stringResourceId);

}
