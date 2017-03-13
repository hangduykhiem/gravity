package fi.zalando.core.utils;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Listener for reporting back to UI when the components are ready for transition.
 * Created by vraisanen on 11.1.2017.
 */
public interface OnReadyForTransitionListener {
    /**
     * Called when UI is ready for transition animation
     * @param sharedElement View that is doing a transformation. Can be null.
     */
    public void onReadyForTransition(@Nullable View sharedElement);
}
