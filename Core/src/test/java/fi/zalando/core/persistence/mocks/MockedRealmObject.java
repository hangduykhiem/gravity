package fi.zalando.core.persistence.mocks;

import fi.zalando.core.data.model.Dateable;
import fi.zalando.core.data.model.annotation.RealmId;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Mocked {@link RealmObject} for test purposes
 *
 * Created by jduran on 22/02/16.
 */
public class MockedRealmObject extends RealmObject implements Dateable {

    @PrimaryKey
    private String id;
    private long savedDate;

    @Override
    public long getSavedDate() {

        return savedDate;
    }

    @Override
    public void setSavedDate(long savedDate) {

        this.savedDate = savedDate;
    }

    @RealmId
    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
