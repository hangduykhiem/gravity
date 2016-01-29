package fi.zalando.core.utils;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for {@link ConvertUtils}
 *
 * Created by hduykhiem on 29/01/16.
 */
public class ConvertUtilsTest {

    @Test
    public void testConvertMeterToFoot(){
        long meter = 202415;
        long foot = ConvertUtils.convertMeterToFoot(meter);
        Assert.assertEquals(foot, 664091);
    }

    @Test
    public void testRoundToTwoDecimal(){
        float numberToRound = 21415.1342523f;
        float result = ConvertUtils.roundsToNearestTwoDecimal(numberToRound);
        Assert.assertEquals(result, 21415.14f);
    }
}
