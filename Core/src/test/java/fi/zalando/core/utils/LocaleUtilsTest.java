package fi.zalando.core.utils;

import org.junit.Test;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@link LocaleUtils} utility class
 *
 * Created by jduran on 26/04/16.
 */
public class LocaleUtilsTest {

    @Test
    public void testLocaliseCurrency() {

        assertEquals("23,15 €", LocaleUtils.localiseCurrency(23.15f, "EUR", Locale.GERMANY));
        assertEquals("23,15 €", LocaleUtils.localiseCurrency(23.15f, "eur", Locale.GERMANY));

        assertEquals("23,15 USD", LocaleUtils.localiseCurrency(23.15f, "USD", Locale.GERMANY));
        assertEquals("23,15 USD", LocaleUtils.localiseCurrency(23.15f, "usd", Locale.GERMANY));

        assertNull(LocaleUtils.localiseCurrency(23.15f, "another currency", Locale.GERMANY));
    }

}
