package fi.zalando.core.utils;

import retrofit2.HttpException;

/**
 * Utility class to help handling {@link Exception}s and {@link Throwable}s
 *
 * Created by jduran on 11/02/16.
 */
public class ThrowableUtils {

    /**
     * Private constructor to avoid instance
     */
    private ThrowableUtils() {
    }

    /**
     * Checks if the given {@link Throwable} is an {@link HttpException} related with the given
     * HttpError code
     *
     * @param throwableToCheck {@link Throwable} to check
     * @param codeToCheck      {@link Integer} with the Http Code to check
     * @return {@link Boolean} indicating if the error is the given Http error
     */
    public static boolean isRetrofitHttpThrowable(Throwable throwableToCheck, int codeToCheck) {

        return throwableToCheck instanceof retrofit2.HttpException
                && ((retrofit2.HttpException) throwableToCheck).code() == codeToCheck;
    }

}
