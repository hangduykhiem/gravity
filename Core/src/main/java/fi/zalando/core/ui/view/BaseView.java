package fi.zalando.core.ui.view;

import rx.Observable;

/**
 * Base interface that will be extended by Activities and Fragments that will define the actions
 * they are able to provide to presenters to render and react with the data.
 *
 * Examples:
 *  - showProgress()
 *  - hideProgress()
 *  - navigateToHome()
 *
 * Created by jduran on 24/11/15.
 */
public abstract interface BaseView {

    /**
     * This method provides an Observable for Presenters to listen to, which will notify them when
     * the UI is initialised.
     * @return {@link Observable}
     */
    public Observable<Void> getOnViewReady();

    /**
     * This is used to initialize the View for the first time. This method is called at the end of
     * onCreate or onActivityCreated by the BasePresenter.
     */
    public void initView();
}
