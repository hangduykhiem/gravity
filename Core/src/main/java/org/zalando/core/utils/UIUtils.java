package org.zalando.core.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;
import org.zalando.core.R;
import org.zalando.core.ui.activity.BaseActivity;

/**
 * Utility class to help with UI related tasks
 */
public class UIUtils {

  /**
   * Private constructor to avoid class instances
   */
  private UIUtils() {
  }

  /**
   * Runs the given {@link Runnable} in the UI thread once the layout is complete. It registers a
   * {@link ViewTreeObserver.OnGlobalLayoutListener} to the given {@link View}, runs the Runnable,
   * and finally unregisters the said listener. This method needs to be called before the layout
   * is finished, e.g. in onCreate.
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
            if (PlatformUtils.isNewerOrEqualSDKVersion(Build.VERSION_CODES
                .JELLY_BEAN)) {
              view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
              view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
          }
        }
    );
  }

  /**
   * Updates the status of the password visibility of the given {@link EditText} to match the
   * given {@link CheckBox}.
   *
   * @param passwordEditText {@link EditText} whose transformation method to update
   * @param showPasswordCheckBox {@link CheckBox} whose status determines the method.
   */
  public static void updatePasswordTransformation(EditText passwordEditText, CheckBox
      showPasswordCheckBox) {
    //Save the selection start and end, as these will reset when changing the transformation:
    final int selectionStart = passwordEditText.getSelectionStart();
    final int selectionEnd = passwordEditText.getSelectionEnd();
    //Update transformation method:
    if (showPasswordCheckBox.isChecked()) {
      passwordEditText.setTransformationMethod(null);
    } else {
      passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
    }
    //Restore original selection:
    passwordEditText.setSelection(selectionStart, selectionEnd);
  }

  /**
   * Get the focus to the first error of the registration
   */
  public static void focusOnFirstError(TextView... textViews) {

    for (TextView textView : textViews) {
      if (!TextUtils.isEmpty(textView.getError())) {
        textView.requestFocus();
        return;
      }
    }
  }

  /**
   * Utility method for building the transition animation for Activities
   *
   * @param launchActivity {@link Activity} to launch from
   * @param launchIntent {@link Intent} to launch
   * @param sharedElements {@link List} of {@link View} {@link String} {@link Pair}s for transition
   * animations
   * @return Animation {@link Bundle} or null if old platform
   */
  @Nullable
  public static Bundle buildAnimationBundle(@NonNull Activity launchActivity,
      @NonNull Intent launchIntent,
      @Nullable List<Pair<View, String>> sharedElements) {
    //Create transition animation for Lollipop & newer:
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        && sharedElements != null
        && !sharedElements.isEmpty()) {
      //Put a flag in the intent notifying about shared element animation:
      launchIntent.putExtra(BaseActivity.HAS_SHARED_ELEMENTS, true);
      return ActivityOptions.makeSceneTransitionAnimation(
          launchActivity,
          toTransitionArray(sharedElements)).toBundle();
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      return ActivityOptions.makeCustomAnimation(
          launchActivity,
          R.anim.activity_slidein_left,
          R.anim.activity_slideout_left).toBundle();
    } else {
      return null;
    }
  }

  /**
   * Transforms a {@link List} of {@link View} {@link String} {@link Pair}s into an array. Used
   * for building material transformation animations.
   *
   * @param list {@link List} to convert into array
   * @return array
   */
  @SuppressWarnings("unchecked")
  private static Pair<View, String>[] toTransitionArray(List<Pair<View, String>> list) {
    final Pair[] varargs = new Pair[list.size()];
    list.toArray(varargs);
    return varargs;
  }
}
