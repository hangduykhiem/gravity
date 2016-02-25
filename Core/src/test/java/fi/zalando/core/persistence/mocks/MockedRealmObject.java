package fi.zalando.core.persistence.mocks;

import android.support.annotation.Nullable;

import fi.zalando.core.data.model.Dateable;
import fi.zalando.core.data.model.Identifiable;
import io.realm.RealmObject;

/**
 * Mocked {@link RealmObject} for test purposes
 *
 * Created by jduran on 22/02/16.
 */
public class MockedRealmObject extends RealmObject implements Dateable, Identifiable {

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

    @Nullable
    @Override
    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
