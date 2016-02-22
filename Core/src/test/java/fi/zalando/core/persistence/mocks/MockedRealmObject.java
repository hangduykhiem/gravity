package fi.zalando.core.persistence.mocks;

import fi.zalando.core.data.model.Dateable;
import io.realm.RealmObject;

/**
 * Mocked {@link RealmObject} for test purposes
 *
 * Created by jduran on 22/02/16.
 */
public class MockedRealmObject extends RealmObject implements Dateable {

    private long savedDate;

    @Override
    public long getSavedDate() {
        return savedDate;
    }

    @Override
    public void setSavedDate(long savedDate) {

        this.savedDate = savedDate;
    }
}
