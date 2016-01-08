package fi.zalando.core.module;

import dagger.Module;
import dagger.Provides;
import fi.zalando.core.ui.animator.ToolbarAnimator;
import fi.zalando.core.ui.animator.impl.ToolbarAnimatorImpl;

/**
 * Dagger module that provides UI related dependencies
 *
 * Created by jduran on 30/11/15.
 */
@Module
public class BaseUIModule {

    @Provides
    public ToolbarAnimator provideToolbarAnimator() {

        return new ToolbarAnimatorImpl();
    }
}
