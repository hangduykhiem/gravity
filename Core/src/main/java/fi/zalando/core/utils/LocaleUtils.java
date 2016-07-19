package fi.zalando.core.utils;

import android.support.annotation.Nullable;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Utility class to ease the localisation tasks
 *
 * Created by jduran on 26/04/16.
 */
public class LocaleUtils {

    //Holds a list of country code and their locales
    private static Map<String, Locale> countryLocales;

    /**
     * Private constructor to avoid method reference
     */
    private LocaleUtils() {
    }

    /**
     * Returns a {@link String} with a localised price using device locale and given parameters
     *
     * @param price        {@link Float} with the price
     * @param currencyCode {@link String} ISO 4217 code.
     * @param localeToUse  {@link Locale} to use for the localisation
     * @return {@link String} localised. Null if code is wrong
     */
    @Nullable
    public static String localiseCurrency(float price, String currencyCode, Locale localeToUse) {

        String priceText = null;
        try {
            Currency currency = Currency.getInstance(currencyCode.toUpperCase(localeToUse));
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(localeToUse);
            numberFormat.setCurrency(currency);
            numberFormat.setMaximumFractionDigits(currency.getDefaultFractionDigits());
            priceText = numberFormat.format(price);
        } catch (IllegalArgumentException e) {
            Timber.e(e, "localiseCurrency error");
        }

        return priceText;
    }

    /**
     * Provides the {@link Locale} of the given country code
     *
     * @param countryCode {@link String} with the ISO country code
     * @return {@link Locale} of the given country code. Null if country locale is not found.
     */
    @Nullable
    public static Locale getCountryLocale(String countryCode) {
        if (countryLocales == null) {
            countryLocales = new HashMap<>();
            for (String iso : Locale.getISOCountries()) {
                Locale l = new Locale("", iso);
                countryLocales.put(iso, l);
            }
        }
        return countryLocales.get(countryCode);
    }

}
