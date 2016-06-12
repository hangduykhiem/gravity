package fi.zalando.core.test;

import fi.zalando.core.domain.BaseService;
import rx.Observable;

import static org.powermock.api.mockito.PowerMockito.doAnswer;

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
     * Creates a {@link Observable.Transformer} that does nothing in order to use for mocking
     * purposes
     *
     * @return {@link Observable.Transformer} to mock
     */
    public static Observable.Transformer createDummyTransformer() {

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
    }

}
