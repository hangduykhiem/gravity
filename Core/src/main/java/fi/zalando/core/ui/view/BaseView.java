package fi.zalando.core.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Base interface that will be extended by Activities and Fragments that will define the actions
 * they are able to provide to presenters to render and react with the data.
 *
 * Examples: - showProgress() - hideProgress() - navigateToHome()
 *
 * Created by jduran on 24/11/15.
 */
public interface BaseView {

  /**
   * This is used to initialize the View for the first time. This method is called at the end of
   * onCreate or onActivityCreated by the BasePresenter.
   *
   * @param initBundle {@link Bundle} to restore or start the instance of the presenter
   */
  void initView(@NonNull Bundle initBundle);
}
