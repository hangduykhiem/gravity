package fi.zalando.core.persistence;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Map;

/**
 * Thread safe class that stores key/value primitive data in the persistent storage
 *
 * Created by jduran on 23/11/15.
 */
public interface PersistentHashTable {

    /**
     * Returns the {@link Boolean} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Boolean} default value if the key doesn't exist
     * @return {@link Boolean} of the given key
     */
    public Boolean get(@NonNull String key, boolean defaultValue);

    /**
     * Saves in the Persistent table the given {@link Boolean} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Boolean} value
     * @param keyValue {@link Boolean} value to link the key
     */
    public void put(@NonNull String key, boolean keyValue);

    /**
     * Returns the {@link String} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link String} default value if the key doesn't exist
     * @return {@link String} of the given key
     */
    public String get(@NonNull String key, String defaultValue);

    /**
     * Saves in the Persistent table the given {@link String} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link String} value
     * @param keyValue {@link String} value to link the key
     */
    public void put(@NonNull String key, String keyValue);

    /**
     * Returns the {@link Long} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Long} default value if the key doesn't exist
     * @return {@link Long} of the given key
     */
    public Long get(@NonNull String key, Long defaultValue);

    /**
     * Saves in the Persistent table the given {@link Long} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Long} value
     * @param keyValue {@link Long} value to link the key
     */
    public void put(@NonNull String key, Long keyValue);

    /**
     * Returns the {@link Integer} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Integer} default value if the key doesn't exist
     * @return {@link Integer} of the given key
     */
    public Integer get(@NonNull String key, Integer defaultValue);

    /**
     * Saves in the Persistent table the given {@link Integer} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Integer} value
     * @param keyValue {@link Integer} value to link the key
     */
    public void put(@NonNull String key, Integer keyValue);

    /**
     * Returns the {@link Date} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Date} default value if the key doesn't exist
     * @return {@link Date} of the given key
     */
    public Date get(@NonNull String key, Date defaultValue);

    /**
     * Saves in the Persistent table the given {@link Date} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Date} value
     * @param keyValue {@link Date} value to link the key
     */
    public void put(@NonNull String key, Date keyValue);

    /**
     * Saves in the Persistent table the objects allocated in the given {@link Map} using the map
     * key as key and value as value in the {@link PersistentHashTable}
     *
     * @param values {@link Map} with the values to store
     */
    public void put(@NonNull Map<String, Object> values);

    /**
     * Clears the {@link PersistentHashTable}
     */
    public void reset();

    /**
     * Checks if the persistent hash table is empty
     *
     * @return {@link Boolean} indicating if the table is empty
     */
    public boolean isEmpty();

    /**
     * Switches the {@link PersistentHashTable} to the given Id. It creates the table if it does not
     * exist
     *
     * @param persistentHashTableId {@link String} with the id of the persistent table to switch to
     */
    public void switchStorage(@NonNull String persistentHashTableId);

}
