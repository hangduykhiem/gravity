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
