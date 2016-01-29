package fi.zalando.core.helper;

import fi.zalando.core.persistence.PersistentHashTable;

/**
 * Base class for setting helper.
 *
 * Created by hduykhiem on 28/01/16.
 */
public abstract class BaseSettingsHelper{

    protected final PersistentHashTable persistentHashTable;

    /**
     * Constructor to create an implement of {@link PersistentHashTable} interface
     * @param persistentHashTable implementation to be created
     */
    protected BaseSettingsHelper(PersistentHashTable persistentHashTable){
        this.persistentHashTable = persistentHashTable;
    }

}
