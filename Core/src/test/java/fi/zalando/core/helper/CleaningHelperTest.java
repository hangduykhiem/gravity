package fi.zalando.core.helper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Test class for {@link CleaningHelper}
 *
 * Created by jduran on 01/03/16.
 */
public class CleaningHelperTest {

    @Mock
    public CleaningHelper.Cleanable cleanable1;
    @Mock
    public CleaningHelper.Cleanable cleanable2;

    private CleaningHelper cleaningHelper;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        CleaningHelper.Cleanable[] cleanables = new CleaningHelper.Cleanable[2];
        cleanables[0] = cleanable1;
        cleanables[1] = cleanable2;

        cleaningHelper = new CleaningHelper();
        cleaningHelper.addCleanables(cleanables);
    }

    @Test
    public void testClean() {

        // make the call
        cleaningHelper.clean();

        // check all the items are cleaned
        verify(cleanable1).clean();
        verify(cleanable2).clean();
    }

}
