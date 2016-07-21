package fi.zalando.core.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test class for {@link ConvertUtils}
 *
 * Created by hduykhiem on 29/01/16.
 */
public class ConvertUtilsTest {

    @Test
    public void testConvertUnits() {

        // Setup units
        final double metres = 1000.0;
        final double kilometres = 1.0;
        final double inches = 39370.1;
        final double foots = 3280.84;
        final double miles = 0.621371;
        final double yards = 1093.61;

        // Convert meters
        assertEquals(metres, ConvertUtils.convert(metres, ConvertUtils.DistanceUnit.METERS,
                ConvertUtils.DistanceUnit.METERS));
        assertEquals(kilometres, ConvertUtils.convert(metres, ConvertUtils.DistanceUnit.METERS,
                ConvertUtils.DistanceUnit.KILOMETERS));
        assertEquals(inches, ConvertUtils.convert(metres, ConvertUtils.DistanceUnit.METERS,
                ConvertUtils.DistanceUnit.INCHES));
        assertEquals(foots, ConvertUtils.convert(metres, ConvertUtils.DistanceUnit.METERS,
                ConvertUtils.DistanceUnit.FOOTS));
        assertEquals(miles, ConvertUtils.convert(metres, ConvertUtils.DistanceUnit.METERS,
                ConvertUtils.DistanceUnit.MILES));
        assertEquals(yards, ConvertUtils.convert(metres, ConvertUtils.DistanceUnit.METERS,
                ConvertUtils.DistanceUnit.YARDS));

        // Convert kilometers
        assertEquals(metres, ConvertUtils.convert(kilometres, ConvertUtils.DistanceUnit.KILOMETERS,
                ConvertUtils.DistanceUnit.METERS));
        assertEquals(kilometres, ConvertUtils.convert(kilometres, ConvertUtils.DistanceUnit
                .KILOMETERS, ConvertUtils.DistanceUnit.KILOMETERS));
        assertEquals(inches, ConvertUtils.convert(kilometres, ConvertUtils.DistanceUnit.KILOMETERS,
                ConvertUtils.DistanceUnit.INCHES));
        assertEquals(foots, ConvertUtils.convert(kilometres, ConvertUtils.DistanceUnit.KILOMETERS,
                ConvertUtils.DistanceUnit.FOOTS));
        assertEquals(miles, ConvertUtils.convert(kilometres, ConvertUtils.DistanceUnit.KILOMETERS,
                ConvertUtils.DistanceUnit.MILES));
        assertEquals(yards, ConvertUtils.convert(kilometres, ConvertUtils.DistanceUnit.KILOMETERS,
                ConvertUtils.DistanceUnit.YARDS));

        // Convert inches
        assertEquals(metres, ConvertUtils.convert(inches, ConvertUtils.DistanceUnit.INCHES,
                ConvertUtils.DistanceUnit.METERS), 1e-3);
        assertEquals(kilometres, ConvertUtils.convert(inches, ConvertUtils.DistanceUnit.INCHES,
                ConvertUtils.DistanceUnit.KILOMETERS), 1e-3);
        assertEquals(inches, ConvertUtils.convert(inches, ConvertUtils.DistanceUnit.INCHES,
                ConvertUtils.DistanceUnit.INCHES), 1e-3);
        assertEquals(foots, ConvertUtils.convert(inches, ConvertUtils.DistanceUnit.INCHES,
                ConvertUtils.DistanceUnit.FOOTS), 1e-3);
        assertEquals(miles, ConvertUtils.convert(inches, ConvertUtils.DistanceUnit.INCHES,
                ConvertUtils.DistanceUnit.MILES), 1e-3);
        assertEquals(yards, ConvertUtils.convert(inches, ConvertUtils.DistanceUnit.INCHES,
                ConvertUtils.DistanceUnit.YARDS), 1e-2);

        // Convert foots
        assertEquals(metres, ConvertUtils.convert(foots, ConvertUtils.DistanceUnit.FOOTS,
                ConvertUtils.DistanceUnit.METERS), 1e-3);
        assertEquals(kilometres, ConvertUtils.convert(foots, ConvertUtils.DistanceUnit.FOOTS,
                ConvertUtils.DistanceUnit.KILOMETERS), 1e-3);
        assertEquals(inches, ConvertUtils.convert(foots, ConvertUtils.DistanceUnit.FOOTS,
                ConvertUtils.DistanceUnit.INCHES), 1e-1);
        assertEquals(foots, ConvertUtils.convert(foots, ConvertUtils.DistanceUnit.FOOTS,
                ConvertUtils.DistanceUnit.FOOTS), 1e-1);
        assertEquals(miles, ConvertUtils.convert(foots, ConvertUtils.DistanceUnit.FOOTS,
                ConvertUtils.DistanceUnit.MILES), 1e-2);
        assertEquals(yards, ConvertUtils.convert(foots, ConvertUtils.DistanceUnit.FOOTS,
                ConvertUtils.DistanceUnit.YARDS), 1e-2);

        // Convert miles
        assertEquals(metres, ConvertUtils.convert(miles, ConvertUtils.DistanceUnit.MILES,
                ConvertUtils.DistanceUnit.METERS), 1e-2);
        assertEquals(kilometres, ConvertUtils.convert(miles, ConvertUtils.DistanceUnit.MILES,
                ConvertUtils.DistanceUnit.KILOMETERS), 1e-2);
        assertEquals(inches, ConvertUtils.convert(miles, ConvertUtils.DistanceUnit.MILES,
                ConvertUtils.DistanceUnit.INCHES), 1e-1);
        assertEquals(foots, ConvertUtils.convert(miles, ConvertUtils.DistanceUnit.MILES,
                ConvertUtils.DistanceUnit.FOOTS), 1e-2);
        assertEquals(miles, ConvertUtils.convert(miles, ConvertUtils.DistanceUnit.MILES,
                ConvertUtils.DistanceUnit.MILES), 1e-2);
        assertEquals(yards, ConvertUtils.convert(miles, ConvertUtils.DistanceUnit.MILES,
                ConvertUtils.DistanceUnit.YARDS), 1e-2);

        // Convert yards
        assertEquals(metres, ConvertUtils.convert(yards, ConvertUtils.DistanceUnit.YARDS,
                ConvertUtils.DistanceUnit.METERS), 1e-2);
        assertEquals(kilometres, ConvertUtils.convert(yards, ConvertUtils.DistanceUnit.YARDS,
                ConvertUtils.DistanceUnit.KILOMETERS), 1e-2);
        assertEquals(inches, ConvertUtils.convert(yards, ConvertUtils.DistanceUnit.YARDS,
                ConvertUtils.DistanceUnit.INCHES), 1);
        assertEquals(foots, ConvertUtils.convert(yards, ConvertUtils.DistanceUnit.YARDS,
                ConvertUtils.DistanceUnit.FOOTS), 1e-1);
        assertEquals(miles, ConvertUtils.convert(yards, ConvertUtils.DistanceUnit.YARDS,
                ConvertUtils.DistanceUnit.MILES), 1e-2);
        assertEquals(yards, ConvertUtils.convert(yards, ConvertUtils.DistanceUnit.YARDS,
                ConvertUtils.DistanceUnit.YARDS), 1e-2);
    }

    @Test
    public void testRoundDecimals() {

        // Round two up
        double numberToRound = 21415.126;
        double result = ConvertUtils.round(numberToRound, 2);
        assertEquals(result, 21415.13);

        // Round two down
        double numberToRound2 = 21415.122;
        double result2 = ConvertUtils.round(numberToRound2, 2);
        assertEquals(result2, 21415.12);

        // Round three up
        numberToRound = 21415.1226;
        result = ConvertUtils.round(numberToRound, 3);
        assertEquals(result, 21415.123);

        // Round three down
        numberToRound2 = 21415.1222;
        result2 = ConvertUtils.round(numberToRound2, 3);
        assertEquals(result2, 21415.122);
    }

    @Test
    public void testGetDisplayCountry(){

        // Test Germany
        assertEquals(ConvertUtils.getDisplayCountry("de"), "Germany");
        // Test Finland
        assertEquals(ConvertUtils.getDisplayCountry("FI"), "Finland");
        // Test Spanish
        assertEquals(ConvertUtils.getDisplayCountry("es"), "Spain");
        // Test Vietnam
        assertEquals(ConvertUtils.getDisplayCountry("VN"), "Vietnam");
    }
}
