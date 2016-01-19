package fi.zalando.core.data.helper.exception;

import com.google.android.gms.common.ConnectionResult;

/**
 * {@link Exception} class to wrap errors related with Google Play Services
 */
public class GooglePlayServicesConnectionResultException extends Exception {

    private final ConnectionResult connectionResult;

    /**
     * Constructor
     *
     * @param description {@link String} with the error description
     * @param result      {@link ConnectionResult} with the error details
     */
    public GooglePlayServicesConnectionResultException(String description, ConnectionResult
            result) {

        super(description);
        this.connectionResult = result;
    }

    /**
     * Provides the {@link ConnectionResult} with the error description
     *
     * @return {@link ConnectionResult} with the error details
     */
    public ConnectionResult getResult() {
        return connectionResult;
    }
}
