package fi.zalando.core.persistence.mocks;

import android.support.annotation.Nullable;

import fi.zalando.core.data.model.Dateable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Mock object to test {@link fi.zalando.core.persistence.BaseRealmDAO}
 *
 * Created by jduran on 07/03/16.
 */
@SuppressWarnings("unused")
public class WrongRealmIdRealmModel extends RealmObject implements Dateable {

    @PrimaryKey
    private String id;
    private long savedDate;

    public WrongRealmIdRealmModel() {

    }

    public WrongRealmIdRealmModel(String id) {

        this.id = id;
    }

    @Nullable
    public Long getSavedDate() {

        return savedDate;
    }

    @Override
    public void setSavedDate(long savedDate) {

        this.savedDate = savedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
