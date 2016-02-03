package fi.zalando.core.ui.listeners;

import android.support.v7.widget.RecyclerView;

/**
 * Implementation of {@link android.view.ViewTreeObserver.OnScrollChangedListener} that calls
 * automatically the hide show method of the {@link OnToolbarHideOrShowListener}
 *
 * Created by jonduran on 12/08/15.
 */
public class HidingToolbarScrollListener extends RecyclerView.OnScrollListener {

    /**
     * Interface that must be implemented by Activities where the hiding/showing toolbar animation
     * capabilities is required
     */
    public interface OnToolbarHideOrShowListener {

        /**
         * Called when the activity should hide the toolbar
         */
        public void hideToolbar();

        /**
         * Called when the activity should show the toolbar
         */
        public void showToolbar();

    }

    private static final int HIDE_THRESHOLD = 20;

    private final OnToolbarHideOrShowListener onToolbarHideOrShowListener;

    // Variables to keep the status of the scrolling
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    /**
     * Constructor
     *
     * @param onToolbarHideOrShowListener {@link OnToolbarHideOrShowListener} listener to call when
     *                                    hide or show is needed
     */
    public HidingToolbarScrollListener(OnToolbarHideOrShowListener onToolbarHideOrShowListener) {

        this.onToolbarHideOrShowListener = onToolbarHideOrShowListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        super.onScrolled(recyclerView, dx, dy);

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }

    /**
     * Gets called when the toolbar should be hidden
     */
    public void onHide() {

        onToolbarHideOrShowListener.hideToolbar();
    }

    /**
     * Gets called when the toolbar should be shown
     */
    public void onShow() {

        onToolbarHideOrShowListener.showToolbar();
    }

}
