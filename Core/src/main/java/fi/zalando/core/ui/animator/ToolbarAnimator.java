package fi.zalando.core.ui.animator;

import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import fi.zalando.core.ui.callback.OnAnimationEnded;

/**
 * Abstract toolbar animators from view logic
 *
 * Created by jduran on 30/11/15.
 */
public interface ToolbarAnimator {

    /**
     * Hides the given {@link Toolbar}. Provide null in the callback parameter to make the change
     * immediately
     *
     * @param toolbarView      {@link Toolbar} to hide
     * @param onAnimationEnded {@link OnAnimationEnded} callback to know when the animation is over.
     *                         Null if no animation is wanted
     */
    public void hideToolbar(final Toolbar toolbarView, @Nullable OnAnimationEnded onAnimationEnded);

    /**
     * Shows the given {@link Toolbar}. Provide null in the callback parameter to make the change
     * immediately
     *
     * @param toolbarView      {@link Toolbar} to show
     * @param onAnimationEnded {@link OnAnimationEnded} callback to know when the animation is over.
     *                         Null if no animation is wanted
     */
    public void showToolbar(final Toolbar toolbarView, @Nullable OnAnimationEnded onAnimationEnded);

}
