package fi.zalando.core.data.helper.throwable;

import com.google.android.gms.common.ConnectionResult;

/**
 * {@link Throwable} class to wrap errors related with Google Play Services
 */
public class GooglePlayServicesConnectionResultThrowable extends Throwable {

    private final ConnectionResult connectionResult;

    /**
     * Constructor
     *
     * @param description {@link String} with the error description
     * @param result      {@link ConnectionResult} with the error details
     */
    public GooglePlayServicesConnectionResultThrowable(String description, ConnectionResult
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
