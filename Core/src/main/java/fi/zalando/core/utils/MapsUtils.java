package fi.zalando.core.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick utils list for Maps
 *
 * Created by hduykhiem on 20/01/16.
 */
public class MapsUtils {


    /**
     * Decode the encoded poly. Found this on the internet, please don't hit me if something went
     * wrong with this.
     *
     * @param encoded encoded poly
     * @return decoded poly
     */
    public static List<LatLng> decodePoly(String encoded) {

        int len = encoded.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encoded.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encoded.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    /**
     * Encodes a sequence of LatLngs into an encoded path string.
     *
     * @param path a list of polylines to encodePolyfromStringbuffer
     */
    public static String encodePoly(final List<LatLng> path) {
        long lastLat = 0;
        long lastLng = 0;

        final StringBuffer result = new StringBuffer();

        for (final LatLng point : path) {
            long lat = Math.round(point.latitude * 1e5);
            long lng = Math.round(point.longitude * 1e5);

            long dLat = lat - lastLat;
            long dLng = lng - lastLng;

            encodePolyfromStringbuffer(dLat, result);
            encodePolyfromStringbuffer(dLng, result);

            lastLat = lat;
            lastLng = lng;
        }
        return result.toString();
    }

    /**
     * The some encoding is done here
     *
     * @param v parameters from lat and long
     * @param result the stringBuffer to append more character
     */
    private static void encodePolyfromStringbuffer(long v, StringBuffer result) {
        v = v < 0 ? ~(v << 1) : v << 1;
        while (v >= 0x20) {
            result.append(Character.toChars((int) ((0x20 | (v & 0x1f)) + 63)));
            v >>= 5;
        }
        result.append(Character.toChars((int) (v + 63)));
    }
}
