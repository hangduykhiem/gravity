package fi.zalando.core.utils;

/**
 * Utilities method to convert units.
 *
 * Created by hduykhiem on 29/01/16.
 */
public class ConvertUtils {

    public enum DistanceUnit {
        /**
         * Distance unit representing Foots in imperial system
         */
        INCHES {
            public double toMeters(double d) {
                return d * 0.0254;
            }

            public double toKilometers(double d) {
                return d * 0.0000254;
            }

            public double toInches(double d) {
                return d;
            }

            public double toFoots(double d) {
                return d * 0.0833333;
            }

            public double toMiles(double d) {
                return d * 1.5783e-5;
            }

            public double toYards(double d) {
                return d * 0.0277778;
            }

            public double convert(double d, DistanceUnit u) {
                return u.toInches(d);
            }
        },
        /**
         * Distance unit representing Foots in imperial system
         */
        FOOTS {
            public double toMeters(double d) {
                return d * 0.3048;
            }

            public double toKilometers(double d) {
                return d * 0.0003048;
            }

            public double toInches(double d) {
                return d * 12;
            }

            public double toFoots(double d) {
                return d;
            }

            public double toMiles(double d) {
                return d * 0.000189394;
            }

            public double toYards(double d) {
                return d * 0.333333;
            }

            public double convert(double d, DistanceUnit u) {
                return u.toFoots(d);
            }
        },
        /**
         * Distance unit representing Kilometers in metric system
         */
        KILOMETERS {
            public double toMeters(double d) {
                return d * 1000.0;
            }

            public double toKilometers(double d) {
                return d;
            }

            public double toInches(double d) {
                return d * 39370.1;
            }

            public double toFoots(double d) {
                return d * 3280.84;
            }

            public double toMiles(double d) {
                return d * 0.621371;
            }

            public double toYards(double d) {
                return d * 1093.61;
            }

            public double convert(double d, DistanceUnit u) {
                return u.toKilometers(d);
            }
        },
        /**
         * Distance unit representing Meters in metric system
         */
        METERS {
            public double toMeters(double d) {
                return d;
            }

            public double toKilometers(double d) {
                return d / 1000.0;
            }

            public double toInches(double d) {
                return d * 39.3701;
            }

            public double toFoots(double d) {
                return d * 3.28084;
            }

            public double toMiles(double d) {
                return d * 0.000621371;
            }

            public double toYards(double d) {
                return d * 1.09361;
            }

            public double convert(double d, DistanceUnit u) {
                return u.toMeters(d);
            }
        },
        /**
         * Distance unit representing Miles in imperial system
         */
        MILES {
            public double toMeters(double d) {
                return d * 1609.34;
            }

            public double toKilometers(double d) {
                return d * 1.60934;
            }

            public double toInches(double d) {
                return d * 63360.0;
            }

            public double toFoots(double d) {
                return d * 5280.0;
            }

            public double toMiles(double d) {
                return d;
            }

            public double toYards(double d) {
                return d * 1760.0;
            }

            public double convert(double d, DistanceUnit u) {
                return u.toMiles(d);
            }
        },
        /**
         * Distance unit representing Yards in imperial system
         */
        YARDS {
            public double toMeters(double d) {
                return d * 0.9144;
            }

            public double toKilometers(double d) {
                return d * 0.0009144;
            }

            public double toInches(double d) {
                return d * 36.0;
            }

            public double toFoots(double d) {
                return d * 3.0;
            }

            public double toMiles(double d) {
                return d * 0.000568182;
            }

            public double toYards(double d) {
                return d;
            }

            public double convert(double d, DistanceUnit u) {
                return u.toYards(d);
            }
        };

        /**
         * Equivalent to {@link #convert(double, DistanceUnit) INCHES.convert(double, this)}.
         *
         * @param distance the distance
         * @return the converted distance
         */
        public double toInches(double distance) {
            throw new AbstractMethodError();
        }

        /**
         * Equivalent to {@link #convert(double, DistanceUnit) FOOTS.convert(double, this)}.
         *
         * @param distance the distance
         * @return the converted distance
         */
        public double toFoots(double distance) {
            throw new AbstractMethodError();
        }

        /**
         * Equivalent to {@link #convert(double, DistanceUnit) KILOMETERS.convert(double, this)}.
         *
         * @param distance the distance
         * @return the converted distance
         */
        public double toKilometers(double distance) {
            throw new AbstractMethodError();
        }

        /**
         * Equivalent to {@link #convert(double, DistanceUnit) METERS.convert(double, this)}.
         *
         * @param distance the distance
         * @return the converted distance
         */
        public double toMeters(double distance) {
            throw new AbstractMethodError();
        }

        /**
         * Equivalent to {@link #convert(double, DistanceUnit) MILES.convert(double, this)}.
         *
         * @param distance the distance
         * @return the converted distance
         */
        public double toMiles(double distance) {
            throw new AbstractMethodError();
        }

        /**
         * Equivalent to {@link #convert(double, DistanceUnit) YARDS.convert(double, this)}.
         *
         * @param distance the distance
         * @return the converted distance
         */
        public double toYards(double distance) {
            throw new AbstractMethodError();
        }

        /**
         * Converts the given distance in the given unit to this unit.
         *
         * @param distance   to convert
         * @param sourceUnit {@link DistanceUnit} of the provided distance
         * @return {@link Double} converted
         */
        public double convert(double distance, DistanceUnit sourceUnit) {
            throw new AbstractMethodError();
        }
    }

    /**
     * Converts given distance from the source {@link DistanceUnit} to target {@link DistanceUnit}
     *
     * @param distance   {@link Double} with the distance to convert
     * @param sourceUnit {@link DistanceUnit} with the source unit
     * @param targetUnit {@link DistanceUnit} with the target unit
     * @return {@link Double} converted
     */
    public static double convert(double distance, DistanceUnit sourceUnit, DistanceUnit
            targetUnit) {

        return targetUnit.convert(distance, sourceUnit);
    }

    /**
     * Method to round to the nearest two decimal
     *
     * @param numberToRound    double number to be rounded
     * @param amountOfDecimals number of decimals
     * @return rounded number
     */
    public static double round(double numberToRound, int amountOfDecimals) {

        final double constantMultiplier = Math.pow(10f, amountOfDecimals);
        return Math.round(numberToRound * constantMultiplier) / constantMultiplier;
    }

}
