package fi.zalando.core.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;

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
     * Runs the given {@link Runnable} in the UI thread once the layout is complete. It registers a
     * {@link ViewTreeObserver.OnGlobalLayoutListener} to the given {@link View}, runs the Runnable,
     * and finally unregisters the said listener. This method needs to be called before the layout
     * is finished, e.g. in onCreate.
     *
     * @param view     View or Layout whose view hierarchy onGlobalLayout to monitor.
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
     * Shows styled {@link Snackbar} with the given text.
     * @param parent parent {@link View}
     * @param resId text to show
     */
    public static void showSnack(@NonNull View parent, @StringRes int resId) {
        Snackbar snackbar = Snackbar.make(parent, resId, Snackbar.LENGTH_LONG);
        styleSnackbar(parent.getContext(), snackbar);
        snackbar.show();
    }

    /**
     * Shows styled {@link Snackbar} with the given text.
     * @param parent parent {@link View}
     * @param text text to show
     */
    public static void showSnack(@NonNull View parent, @NonNull CharSequence text) {
        Snackbar snackbar = Snackbar.make(parent, text, Snackbar.LENGTH_LONG);
        styleSnackbar(parent.getContext(), snackbar);
        snackbar.show();
    }

    /**
     * Shows styled {@link Snackbar} with the given text and button
     * @param parent parent {@link View}
     * @param resId text resource id
     * @param buttonTextId button text resource id
     * @param onClickAction {@link Runnable} to execute on button press
     */
    public static void showSnack(@NonNull View parent, @StringRes int resId,
                                 @StringRes int buttonTextId, @Nullable Runnable onClickAction) {
        Snackbar snackbar = Snackbar.make(parent, resId, Snackbar.LENGTH_INDEFINITE);
        if (onClickAction != null) {
            snackbar.setAction(buttonTextId, (v) -> onClickAction.run());
        }
        styleSnackbar(parent.getContext(), snackbar);
        snackbar.show();
    }

    /**
     * Styles the given Snackbar based on the styles defined in application styles with a name
     * "SnackbarStyle".
     * @param context {@link Context}
     * @param snackbar {@link Snackbar}
     */
    @SuppressWarnings("ResourceType") //Solves lint bug
    private static void styleSnackbar(Context context, Snackbar snackbar) {
        View snackView = snackbar.getView();

        //Get the styles defined in the XML:
        int[] attrs = {android.R.attr.textColor, android.R.attr.background};
        int textColor = Color.BLACK;
        int backgroundColor = Color.WHITE;

        int resID = context.getResources().getIdentifier("SnackbarStyle" , "style",
                context.getPackageName());

        //Try to fetch the style attributes
        try {
            TypedArray ta = context.getTheme().obtainStyledAttributes(resID, attrs);
            try {
                textColor = ta.getColor(0, Color.BLACK);
                backgroundColor = ta.getColor(1, Color.WHITE);
            } finally {
                ta.recycle();
            }
        } catch (Resources.NotFoundException e) { //Attributes could not be found
            //ignore
        }

        //Apply the styles:
        snackView.setBackgroundColor(backgroundColor);
        snackbar.setActionTextColor(textColor);
    }
}
