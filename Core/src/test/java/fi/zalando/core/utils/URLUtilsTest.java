package fi.zalando.core.utils;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import fi.zalando.core.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@link URLUtils}
 *
 * Created by jduran on 17/03/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
        "src/main/AndroidManifest.xml")
public class URLUtilsTest {

    @Test
    public void testExtractQueryParameter() {

        // Test empty string
        String emptyString = "";
        assertEquals(0, URLUtils.extractQueryParameters(emptyString, "q").size());

        // Test parsing different params
        String urlWithParameters = "/api/de/abstracts?seq=MjAwOjEwMA&q=red";
        assertEquals("MjAwOjEwMA", URLUtils.extractQueryParameters(urlWithParameters, "seq").get
                (0));
        assertEquals("red", URLUtils.extractQueryParameters(urlWithParameters, "q").get(0));
        assertEquals(0, URLUtils.extractQueryParameters(urlWithParameters, "anotherParam").size());

        // Test multiple same parameters
        String urlWithMultipleParameters = "/api/de/abstracts?q=MjAwOjEwMA&q=red&q=another";
        assertEquals(3, URLUtils.extractQueryParameters(urlWithMultipleParameters, "q").size());
        assertTrue(URLUtils.extractQueryParameters(urlWithMultipleParameters, "q").contains
                ("MjAwOjEwMA"));
        assertTrue(URLUtils.extractQueryParameters(urlWithMultipleParameters, "q").contains("red"));
        assertTrue(URLUtils.extractQueryParameters(urlWithMultipleParameters, "q").contains
                ("another"));
    }

    @Test
    public void testExtractLastSegmentFromUrl() {

        assertEquals("42", URLUtils.extractLastSegmentFromUrl("http://example" +
                ".com/foo/bar/42?param=true"));
        assertEquals("foo", URLUtils.extractLastSegmentFromUrl("http://example.com/foo"));
        assertEquals("bar", URLUtils.extractLastSegmentFromUrl("http://example.com/bar/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractLastSegmentFromUrlFailed() {

        URLUtils.extractLastSegmentFromUrl("invalid URL");
    }

}
