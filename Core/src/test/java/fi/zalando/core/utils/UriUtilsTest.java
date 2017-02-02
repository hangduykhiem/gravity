package fi.zalando.core.utils;

import android.net.Uri;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

import fi.zalando.core.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link UriUtils} Created by jduran on 02/02/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
        "src/main/AndroidManifest.xml")
public class UriUtilsTest {

    @Test
    public void shouldReplaceQueryParameter() {
        Uri uri = Uri.parse("http://example" +
                ".com?utm_source=Indeed&utm_medium=organic&utm_campaign=Indeed");
        Uri uriExpected = Uri.parse("http://example" +
                ".com?utm_source=indeed&utm_medium=organic&utm_campaign=Indeed");

        assertThat(UriUtils.replaceUriParameter(uri, "utm_source",
                "Indeed".toLowerCase(Locale.getDefault()))).isEqualTo(uriExpected);
    }

    @Test
    public void shouldNotReplaceQueryParameter() {
        Uri uri = Uri.parse("http://example" +
                ".com?utm_source=Indeed&utm_medium=organic&utm_campaign=Indeed");
        assertThat(UriUtils.replaceUriParameter(uri, "doesNotExist",
                "Indeed".toLowerCase(Locale.getDefault()))).isEqualTo(uri);
    }

}
