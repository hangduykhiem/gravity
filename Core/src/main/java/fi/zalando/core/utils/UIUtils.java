package fi.zalando.core.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Utility class to help with UI related tasks
 *
 * Created by vraisanen on 16/02/16.
 */
public class UIUtils {

    /**
     * Private constructor to avoid class instances
     */
    private UIUtils() {
    }

    /**
     * Runs the given {@link Runnable} synchronously in the UI thread once the layout is complete.
     * It registers a {@link ViewTreeObserver.OnGlobalLayoutListener} to the given {@link View},
     * runs the Runnable, and finally unregisters the said listener. This method needs to be called
     * before the layout is finished, e.g. in onCreate.
     *
     * @param view View or Layout whose view hierarchy onGlobalLayout to monitor.
     * @param runnable Runnable to be run once the layout is finished.
     */
    public static void runOnGlobalLayout(@NonNull View view, @NonNull Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onGlobalLayout() {
                        //Run the runnable:
                        runnable.run();
                        //Unregister the listener so that the runnable is only run once:
                        if (Build.VERSION.SDK_INT < 16) {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
        );
    }
}
