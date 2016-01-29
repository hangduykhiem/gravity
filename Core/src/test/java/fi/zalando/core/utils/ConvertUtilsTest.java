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
        // Round up
        float numberToRound = 21415.126f;
        float result = ConvertUtils.roundsToNearestTwoDecimal(numberToRound);
        Assert.assertEquals(result, 21415.13f);

        // Round down
        float numberToRound2 = 21415.122f;
        float result2 = ConvertUtils.roundsToNearestTwoDecimal(numberToRound2);
        Assert.assertEquals(result2, 21415.12f);
    }
}
