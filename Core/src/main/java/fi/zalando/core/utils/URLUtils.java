package fi.zalando.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Utility class to help when using URLs
 *
 * Created by jduran on 17/03/16.
 */
public class URLUtils {

    /**
     * Private constructor to avoid object instances
     */
    private URLUtils() {
    }

    /**
     * Extracts the given {@link String} with the query parameter from the given {@link String} url
     *
     * @param urlToCheck     {@link String} with the url to analyse
     * @param paramToExtract {@link String} with the parameter to get the value from
     * @return {@link List} with the extracted parameter {@link String}s. Empty list if not found
     */
    public static List<String> extractQueryParameters(String urlToCheck, String paramToExtract) {

        try {
            // Get all query parameters
            Map<String, List<String>> queryParams = getQueryParams(urlToCheck);
            List<String> extractedParams = queryParams.get(paramToExtract);
            // If found return it, if not, empty list
            if (extractedParams != null && !extractedParams.isEmpty()) {
                return extractedParams;
            }
        } catch (UnsupportedEncodingException e) {
            Timber.e(e, "extractQueryParameters");
        }

        return new ArrayList<>();
    }

    /**
     * Provides a {@link Map} with all the parameters key/value pairs in the given {@link String}
     * url
     *
     * @param url {@link String} with the url to analyse
     * @return {@link Map} with all the key/value pairs
     * @throws UnsupportedEncodingException if parsing failed
     */
    private static Map<String, List<String>> getQueryParams(String url) throws
            UnsupportedEncodingException {

        Map<String, List<String>> params = new HashMap<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length < 2) {
            return params;
        }

        String query = urlParts[1];
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            String key = URLDecoder.decode(pair[0], "UTF-8");
            String value = "";
            if (pair.length > 1) {
                value = URLDecoder.decode(pair[1], "UTF-8");
            }

            // skip ?& and &&
            if ("".equals(key) && pair.length == 1) {
                continue;
            }

            List<String> values = params.get(key);
            if (values == null) {
                values = new ArrayList<>();
                params.put(key, values);
            }
            values.add(value);
        }

        return params;
    }

}
