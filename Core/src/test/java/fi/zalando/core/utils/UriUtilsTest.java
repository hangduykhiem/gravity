package fi.zalando.core.utils;

import static org.assertj.core.api.Assertions.assertThat;

import android.net.Uri;
import android.os.Build;
import fi.zalando.core.BuildConfig;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test class for {@link UriUtils} Created by jduran on 02/02/17.
 */
@RunWith(RobolectricTestRunner.class)
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
