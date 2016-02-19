package fi.zalando.core.data.model;

import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;

/**
 * Abstract {@link RealmObject} that keeps saved when the object was saved in the persistence
 * storage. NOTE: Abstract keyword is not used because realm processor fails.
 */
public class BaseRealmObject extends RealmObject {

    private long savedDate;

    /**
     * Returns the saved date
     *
     * @return {@link Long} with the saved date
     */
    public long getSavedDate() {

        return savedDate;
    }

    /**
     * Sets the saved time
     *
     * @param savedDate {@link Long} with the saved time
     */
    public void setSavedDate(long savedDate) {

        this.savedDate = savedDate;
    }

    /**
     * Utility method to check if the object is expired according to given parameters
     *
     * @param liveTime {@link Long} with the live time
     * @param timeUnit {@link TimeUnit} of the given live time
     * @return {@link Boolean} indicating if the object is expired
     */
    public static boolean hasExpired(BaseRealmObject realmObject, long liveTime, TimeUnit
            timeUnit) {
        // Needs to be static because realm processor fails since it is not accessing the method
        return realmObject.getSavedDate() + timeUnit.toMillis(liveTime) <= System
                .currentTimeMillis();
    }
}
