package fi.zalando.core.test;

import static org.powermock.api.mockito.PowerMockito.doAnswer;

import fi.zalando.core.domain.BaseService;
import io.reactivex.ObservableTransformer;

/**
 * Utility class to help in test cases
 *
 * Created by jduran on 12/02/16.
 */
public class TestUtils {

    /**
     * Private constructor to avoid object instances
     */
    private TestUtils() {
    }

    /**
     * Creates a {@link ObservableTransformer} that does nothing in order to use for mocking
     * purposes
     *
     * @return {@link ObservableTransformer} to mock
     */
    public static ObservableTransformer createDummyTransformer() {

        return transformer -> transformer;
    }

    /**
     * Setups the modcked {@link BaseService}
     *
     * @param domainService {@link BaseService} to add the mocked apply schedulers logic
     */
    public static void setupMockedDomainService(BaseService domainService) {

        // Mock the apply schedulers so the call to real object won't crash
        doAnswer(invocation -> TestUtils.createDummyTransformer()).when(domainService)
                .applySchedulersToObservable();
        doAnswer(invocation -> TestUtils.createDummyTransformer()).when(domainService)
                .applySchedulersToCompletable();
        doAnswer(invocation -> TestUtils.createDummyTransformer()).when(domainService)
                .applySchedulersToSingle();
    }

}
