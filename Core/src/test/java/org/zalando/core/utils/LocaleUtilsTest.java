package org.zalando.core.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.util.Locale;
import org.junit.Test;

/**
 * Test class for {@link LocaleUtils} utility class
 */
public class LocaleUtilsTest {

  @Test
  public void testLocaliseCurrency() {

    assertEquals("23,15 €", LocaleUtils.localiseCurrency(23.15, "EUR", Locale.GERMANY));
    assertEquals("23,15 €", LocaleUtils.localiseCurrency(23.15, "eur", Locale.GERMANY));

    assertEquals("23,15 USD", LocaleUtils.localiseCurrency(23.15, "USD", Locale.GERMANY));
    assertEquals("23,15 USD", LocaleUtils.localiseCurrency(23.15, "usd", Locale.GERMANY));

    assertNull(LocaleUtils.localiseCurrency(23.15, "another currency", Locale.GERMANY));
  }
}
