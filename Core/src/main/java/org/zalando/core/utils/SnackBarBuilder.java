package org.zalando.core.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import java.util.Arrays;
import org.zalando.core.R;

/**
 * Untils to create and display a {@link Snackbar}
 */
public class SnackBarBuilder {

  private final View parentView;

  private String text;
  private String buttonText;
  private int textColor;
  private int maxLines;
  private int backgroundColorRes;
  private int actionColorRes;
  private Runnable action;
  private int duration = Snackbar.LENGTH_SHORT;

  /**
   * Constructor for the builder
   *
   * @param parentView {@link View} parentView of the snackBar
   */
  public SnackBarBuilder(@NonNull View parentView) {
    this.parentView = parentView;
  }

  /**
   * Set text for the SnackBar
   *
   * @param charSequence {@link CharSequence} text to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setText(@NonNull CharSequence charSequence) {
    text = charSequence.toString();
    return this;
  }

  /**
   * Set text for the SnackBar
   *
   * @param stringRes {@link StringRes} text from String to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setText(@StringRes int stringRes) {
    text = parentView.getContext().getString(stringRes);
    return this;
  }

  /**
   * Set background color for the SnackBar
   *
   * @param colorRes {@link StringRes} color to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setBackgroundColor(@ColorRes int colorRes) {
    this.backgroundColorRes = colorRes;
    return this;
  }

  /**
   * Set action color for the SnackBar
   *
   * @param colorRes {@link StringRes} color to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setActionColor(@ColorRes int colorRes) {
    this.actionColorRes = colorRes;
    return this;
  }

  /**
   * Set action color for the SnackBar
   *
   * @param onClickAction {@link StringRes} action to perform when click
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setOnClickAction(@NonNull Runnable onClickAction) {
    this.action = onClickAction;
    return this;
  }

  /**
   * Set duration of the SnackBar
   *
   * @param duration to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setDuration(@Snackbar.Duration int duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Set text for SnackBar button
   *
   * @param buttonText {@link CharSequence} to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setButtonText(@NonNull CharSequence buttonText) {
    this.buttonText = buttonText.toString();
    return this;
  }

  /**
   * Set text for the SnackBar button
   *
   * @param stringRes {@link StringRes} text from String to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setButtonText(@StringRes int stringRes) {
    this.buttonText = parentView.getContext().getString(stringRes);
    return this;
  }

  /**
   * Set text color for the SnackBar button
   *
   * @param colorRes {@link ColorRes} color to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setTextColor(@ColorRes int colorRes) {
    this.textColor = colorRes;
    return this;
  }

  /**
   * Set max line of text for snackbar
   *
   * @param maxLines to set
   * @return {@link SnackBarBuilder} to continue building SnackBar
   */
  public SnackBarBuilder setMaxLines(int maxLines) {
    this.maxLines = maxLines;
    return this;
  }


  /**
   * Build and display the snackbar.
   */
  public Snackbar build() {
    Preconditions.checkNotNull(text, "SnackBar text has not been set");
    final Snackbar snackbar = Snackbar.make(parentView, text, duration);

    styleSnackbar(parentView.getContext(), snackbar);

    if (action != null) {
      snackbar.setAction(buttonText, (v) -> action.run());

      if (actionColorRes != 0) {
        snackbar.setActionTextColor(ContextCompat.getColor(parentView.getContext(),
            actionColorRes));
      }
    }

    if (backgroundColorRes != 0) {
      snackbar.getView().setBackgroundColor(
          ContextCompat.getColor(parentView.getContext(), backgroundColorRes));
    }

    if (textColor != 0) {
      ((TextView) snackbar.getView().findViewById(R.id.snackbar_text))
          .setTextColor(ContextCompat.getColor(parentView.getContext(), textColor));
    }

    if (maxLines != 0) {
      ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setMaxLines(maxLines);
    }

    return snackbar;
  }

  /**
   * Styles the given Snackbar based on the styles defined in application styles with a name
   * "SnackbarStyle".
   *
   * @param context {@link Context}
   * @param snackbar {@link Snackbar}
   */
  @SuppressWarnings("ResourceType") //Solves lint bug
  private static void styleSnackbar(Context context, Snackbar snackbar) {
    View snackView = snackbar.getView();

    //Get the styles defined in the XML:
    int[] attrs = {android.R.attr.textColor,
        android.R.attr.background,
        android.R.attr.textColorHighlight};

    // Sort attrs array to order, to do binary seach.
    Arrays.sort(attrs);

    int textColor = Color.BLACK;
    int actionTextColor = Color.BLACK;
    int backgroundColor = Color.WHITE;

    int resID = context.getResources().getIdentifier("SnackbarStyle", "style",
        context.getPackageName());

    //Try to fetch the style attributes
    try {
      TypedArray ta = context.getTheme().obtainStyledAttributes(resID, attrs);
      try {

        // Fetch the style attributes. Array has to be sorted to fetch the correct value.
        textColor = ta.getColor(Arrays.binarySearch(attrs, android.R.attr.textColor),
            Color.BLACK);
        backgroundColor = ta.getColor(Arrays.binarySearch(attrs, android.R.attr.background),
            Color.WHITE);
        actionTextColor = ta.getColor(Arrays.binarySearch(
            attrs, android.R.attr.textColorHighlight), Color.BLACK);

      } finally {
        ta.recycle();
      }
    } catch (Resources.NotFoundException e) { //Attributes could not be found
      //ignore
    }

    //Apply the styles:
    snackView.setBackgroundColor(backgroundColor);
    ((TextView) snackView.findViewById(R.id.snackbar_text))
        .setTextColor(textColor);
    snackbar.setActionTextColor(actionTextColor);

  }
}
