package org.zalando.core.ui.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import org.zalando.core.ui.activity.ReusableFragmentActivity;
import org.zalando.core.ui.fragment.BaseFragment;

/**
 * View definition for {@link ReusableFragmentActivity}
 */
public interface ReusableFragmentActivityView extends BaseView {

  /**
   * Replaces the fragment container with the given {@link Fragment} using no animations or back
   * stack.
   *
   * @param fragment {@link BaseFragment} to switch to.
   */
  void switchFragment(@NonNull BaseFragment fragment);

  /**
   * Replaces the fragment container with the given fragment
   *
   * @param fragment {@link BaseFragment} that will replace the previous one
   * @param addToBackStack True if wanted to add to BackStack false otherwise
   * @param animate True if you want to animate the change
   */
  void switchFragment(BaseFragment fragment, boolean addToBackStack, boolean animate);

  /**
   * Sets the toolbar title to the given String.
   *
   * @param title The title to use
   */
  void setToolbarTitle(String title);

  /**
   * Sets the toolbar title to the given String.
   *
   * @param stringResourceId The title to use
   */
  void setToolbarTitle(@StringRes int stringResourceId);

}
