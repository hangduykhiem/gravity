package fi.zalando.core.utils;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

/**
 * Test class for {@link MapsUtils}
 *
 * Created by hduykhiem on 21/01/16.
 */
public class MapsUtilsTest {

    private static final String POLYLINES_ENCODED="qtbxFfjbcMf@AHAFA^MBA";

    @Test
    public void testDecodingAndEncodingPolylines(){

        List<LatLng> decodedPolylines = MapsUtils.decodePoly(POLYLINES_ENCODED);
        String encodedPolylines = MapsUtils.encodePoly(decodedPolylines);

        Assert.assertEquals(encodedPolylines,POLYLINES_ENCODED);
        Assert.assertEquals(decodedPolylines.get(0).latitude,40.81497);
        Assert.assertEquals(decodedPolylines.get(0).longitude,-74.07284);
        Assert.assertEquals(decodedPolylines.get(decodedPolylines.size()-1).latitude,40.81450);
        Assert.assertEquals(decodedPolylines.get(decodedPolylines.size()-1).longitude,-74.07273);
    }

}
