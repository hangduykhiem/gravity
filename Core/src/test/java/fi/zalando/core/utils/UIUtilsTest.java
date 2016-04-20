package fi.zalando.core.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * Test class for {@link UIUtils}
 *
 * Created by jduran on 22/02/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PlatformUtils.class)
public class UIUtilsTest {

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Test
    public void testRunOnGlobalLayout() throws Exception {

        // Mock API Check
        PowerMockito.spy(PlatformUtils.class);
        // Say we are in ICS
        doReturn(Build.VERSION_CODES
                .ICE_CREAM_SANDWICH_MR1).when(PlatformUtils.class, "getOSVersion");

        ViewTreeObserver viewTreeObserver = mock(ViewTreeObserver.class);
        View view = mock(View.class);
        Runnable runnable = mock(Runnable.class);
        // Mock answer to provide view tree observer
        doAnswer(invocation -> viewTreeObserver).when(view).getViewTreeObserver();
        // Mock callback answer
        doAnswer(invocation -> {
            ((ViewTreeObserver.OnGlobalLayoutListener) invocation.getArguments()[0])
                    .onGlobalLayout();
            return null;
        }).when(viewTreeObserver).addOnGlobalLayoutListener(any(ViewTreeObserver
                .OnGlobalLayoutListener.class));

        UIUtils.runOnGlobalLayout(view, runnable);

        // Verify addOnGlobalLayoutListener called
        verify(viewTreeObserver, times(1)).addOnGlobalLayoutListener(any());
        // Check runnable is called
        verify(runnable, times(1)).run();
        // Check listener is removed
        verify(viewTreeObserver, times(1)).removeGlobalOnLayoutListener(any());

        // Change now to LOLLIPOP
        doReturn(Build.VERSION_CODES
                .LOLLIPOP).when(PlatformUtils.class, "getOSVersion");
        // Make call again
        UIUtils.runOnGlobalLayout(view, runnable);
        // Check listener is removed
        verify(viewTreeObserver, times(1)).removeOnGlobalLayoutListener(any());
    }

}
