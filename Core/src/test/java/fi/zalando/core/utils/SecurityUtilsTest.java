package fi.zalando.core.utils;

import android.os.Build;
import android.support.v4.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import fi.zalando.core.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

/**
 * Test class for {@link SecurityUtils} utility class
 *
 * Created by jduran on 12/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
        "src/main/AndroidManifest.xml")
public class SecurityUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testFailingToDecryptWhenUsingDifferentKey() throws GeneralSecurityException,
            UnsupportedEncodingException {

        String textToEncrypt = "Text to encrypt!";

        Pair<String, String> encryptionDuple = SecurityUtils.encrypt(textToEncrypt);

        // Check that we can decrypt it using the same key
        assertNotSame(SecurityUtils.decrypt(encryptionDuple.first, "AnotherKey to force crash"),
                textToEncrypt);
    }

    @Test
    public void testGeneratingRandomKey() throws GeneralSecurityException {

        assertNotNull(SecurityUtils.generateRandomKey());
    }

    @Test
    public void testGeneratingString() throws GeneralSecurityException {

        assertNotNull(SecurityUtils.generateRandomString());
    }

    @Test
    public void testGenerateKeyAlwaysSame() throws GeneralSecurityException {

        String textToEncrypt = "Password to use";

        String initialKey = SecurityUtils.generateKey(textToEncrypt);
        String secondKey = SecurityUtils.generateKey(textToEncrypt);

        assertEquals(initialKey, secondKey);
    }

    @Test
    public void testEncryptionDecryptionRandomKey() throws GeneralSecurityException,
            UnsupportedEncodingException {

        String textToEncrypt = "Text to encrypt!";

        Pair<String, String> encryptionDuple = SecurityUtils.encrypt(textToEncrypt);
        // Check that something was returned
        assertNotNull(encryptionDuple);

        // Check that we can decrypt it using the same key
        assertEquals(SecurityUtils.decrypt(encryptionDuple.first, encryptionDuple.second),
                textToEncrypt);
    }

    @Test
    public void testEncryptionWithSameKeyAlwaysSame() throws GeneralSecurityException,
            UnsupportedEncodingException {

        String textToEncrypt = "Password to use";

        String encryptedText = SecurityUtils.encryptKey(textToEncrypt);
        String encryptedText2 = SecurityUtils.encryptKey(textToEncrypt);

        // Check that we can decrypt it using the same key
        assertEquals(encryptedText, encryptedText2);
    }

}
