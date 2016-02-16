package fi.zalando.core.test;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import fi.zalando.core.domain.BaseService;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Utility class to help in test cases
 *
 * Created by jduran on 12/02/16.
 */
public class TestUtils {

    /**
     * Private constructor to avoid object instances
     */
    protected TestUtils() {
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
     * Provides the String from the given file
     *
     * @param file {@link File} to load the data from
     * @return {@link String} in the given file
     * @throws IOException If reading file failed
     */
    public static String loadStringFromFile(@NonNull File file) throws IOException {

        final StringBuilder sb = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new
                FileInputStream(file), "UTF-8"));
        String strLine;
        while ((strLine = reader.readLine()) != null) {
            sb.append(strLine);
        }
        return sb.toString();
    }

    /**
     * Setups the modcked {@link BaseService}
     *
     * @param domainService {@link BaseService} to add the mocked apply schedulers logic
     */
    public static void setupMockedDomainService(BaseService domainService) {

        // Mock the apply schedulers so the call to real object won't crash
        doAnswer(invocation -> TestUtils.createDummyTransformer()).when(domainService)
                .applySchedulers();
    }

    /**
     * Setup the mocked Android {@link TextUtils}
     */
    public static void setupMockedTextUtils() {

        mockStatic(TextUtils.class);

        when(TextUtils.equals(any(CharSequence.class), any(CharSequence.class))).thenAnswer
                (invocation -> {
                    CharSequence a = (CharSequence) invocation.getArguments()[0];
                    CharSequence b = (CharSequence) invocation.getArguments()[1];
                    if (a == b) return true;
                    int length;
                    if (a != null && b != null && (length = a.length()) == b.length()) {
                        if (a instanceof String && b instanceof String) {
                            return a.equals(b);
                        } else {
                            for (int i = 0; i < length; i++) {
                                if (a.charAt(i) != b.charAt(i)) return false;
                            }
                            return true;
                        }
                    }
                    return false;
                });

        when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer
                (invocation -> {
                    CharSequence str = (CharSequence) invocation.getArguments()[0];
                    return str == null || str.length() == 0;
                });
    }
}
