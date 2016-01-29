package fi.zalando.core.utils;

/**
 * Utilities method to convert units.
 *
 * Created by hduykhiem on 29/01/16.
 */
public class ConvertUtils {

    /**
     * Method to convert meter to foot
     * @param meter to be converted
     * @return converted foot
     */
    public static float convertMeterToFoot(long meter){
        return meter * 3.28084f;
    }

    /**
     * Method to round to the nearest two decimal
     *
     * @param numberToRounds float number to be rounded
     * @return rounded number
     */
    public static float roundsToNearestTwoDecimal(float numberToRounds){
        return Math.round(numberToRounds*100.f)/100.f;
    }

}
