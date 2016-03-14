package fi.zalando.core.persistence.mocks;

import android.support.annotation.Nullable;

import fi.zalando.core.data.model.Dateable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Mock object with wrong primary key
 *
 * Created by jduran on 07/03/16.
 */
@SuppressWarnings("unused")
public class WrongPrimaryKeyRealmModel extends RealmObject implements Dateable {

    @PrimaryKey
    private short id;
    private long savedDate;

    @Nullable
    public Long getSavedDate() {

        return savedDate;
    }

    @Override
    public void setSavedDate(long savedDate) {

        this.savedDate = savedDate;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }
}
