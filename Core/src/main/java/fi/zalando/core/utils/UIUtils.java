package fi.zalando.core.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import timber.log.Timber;

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
    public static void runOnGlobalLayout(View view, Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onGlobalLayout() {
                        runnable.run();
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
