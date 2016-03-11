package fi.zalando.core.persistence.mocks;

import android.support.annotation.Nullable;

import fi.zalando.core.data.model.Dateable;
import fi.zalando.core.data.model.annotation.RealmId;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Mock object to test {@link fi.zalando.core.persistence.BaseRealmDAO}
 *
 * Created by jduran on 07/03/16.
 */
@SuppressWarnings("unused")
public class ValidRealmModel extends RealmObject implements Dateable {

    @PrimaryKey
    private String id;
    private long savedDate;

    public ValidRealmModel() {

    }

    public ValidRealmModel(String id) {

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

    @RealmId
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
