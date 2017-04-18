package org.zalando.core.ui.callback;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Listener for reporting back to UI when the components are ready for transition.
 */
public interface OnReadyForTransitionListener {

  /**
   * Called when UI is ready for transition animation
   *
   * @param sharedElement View that is doing a transformation. Can be null.
   */
  void onReadyForTransition(@Nullable View sharedElement);
}
