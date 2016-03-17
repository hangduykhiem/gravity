package fi.zalando.core.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@link URLUtils}
 *
 * Created by jduran on 17/03/16.
 */
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

}
