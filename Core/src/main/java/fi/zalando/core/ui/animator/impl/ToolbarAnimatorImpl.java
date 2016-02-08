package fi.zalando.core.ui.animator.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import fi.zalando.core.ui.animator.ToolbarAnimator;
import fi.zalando.core.ui.callback.OnAnimationEnded;
import timber.log.Timber;

/**
 * Base implementation of {@link ToolbarAnimator}
 *
 * Created by jduran on 30/11/15.
 */
public class ToolbarAnimatorImpl implements ToolbarAnimator {

    @Override
    public void hideToolbar(Toolbar toolbarView, @Nullable OnAnimationEnded onAnimationEnded) {

        Timber.d("hideToolbar animated? " + (onAnimationEnded != null));

        toolbarView.post(() -> {

            ValueAnimator toolbarShowAnim =
                    ObjectAnimator.ofFloat(toolbarView, "translationY", -toolbarView.getBottom());
            toolbarShowAnim.setDuration(onAnimationEnded != null ? toolbarView.getContext()
                    .getResources().getInteger(android.R.integer.config_shortAnimTime) : 0);
            // Do not add the listener and animation definitions if null is provided as listener
            if (onAnimationEnded != null) {
                toolbarShowAnim.setInterpolator(new AccelerateInterpolator(2));
                toolbarShowAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onAnimationEnded.onAnimationEnded();
                    }
                });
            }
            toolbarShowAnim.start();
        });
    }

    @Override
    public void showToolbar(Toolbar toolbarView, @Nullable OnAnimationEnded onAnimationEnded) {

        Timber.d("showToolbar animated? " + (onAnimationEnded != null));

        toolbarView.post(() -> {

            // Only animate if the toolbar is not in place
            if (toolbarView.getTranslationY() != 0) {

                ValueAnimator toolbarShowAnim = ObjectAnimator.ofFloat(toolbarView, "translationY",
                        -toolbarView.getBottom(), 0);

                toolbarShowAnim.setDuration(onAnimationEnded != null ? toolbarView.getContext()
                        .getResources().getInteger(android.R.integer.config_shortAnimTime) : 0);
                // Do not add the listener and animation definitions if null is provided as listener
                if (onAnimationEnded != null) {
                    toolbarShowAnim.setInterpolator(new DecelerateInterpolator(2));
                    toolbarShowAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            onAnimationEnded.onAnimationEnded();
                        }
                    });
                    toolbarShowAnim.start();
                }
            }
        });
    }
}
