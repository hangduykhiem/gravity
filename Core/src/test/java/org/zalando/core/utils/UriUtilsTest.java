package org.zalando.core.utils;

import static junit.framework.Assert.assertEquals;

import android.net.Uri;
import android.os.Build;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.zalando.core.BuildConfig;

/**
 * Test class for {@link UriUtils}
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
    sdk = Build.VERSION_CODES.LOLLIPOP,
    manifest = "src/main/AndroidManifest.xml")
public class UriUtilsTest {

  @Test
  public void shouldReplaceQueryParameter() {
    Uri uri = Uri.parse("http://example" +
        ".com?utm_source=Indeed&utm_medium=organic&utm_campaign=Indeed");
    Uri uriExpected = Uri.parse("http://example" +
        ".com?utm_source=indeed&utm_medium=organic&utm_campaign=Indeed");

    assertEquals(uriExpected,
        UriUtils.replaceUriParameter(uri, "utm_source", "Indeed".toLowerCase(Locale.getDefault())));
  }

  @Test
  public void shouldNotReplaceQueryParameter() {
    Uri uri = Uri
        .parse("http://example.com?utm_source=Indeed&utm_medium=organic&utm_campaign=Indeed");
    assertEquals(uri, UriUtils
        .replaceUriParameter(uri, "doesNotExist", "Indeed".toLowerCase(Locale.getDefault())));
  }

}
