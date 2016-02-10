package fi.zalando.core.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Date;
import java.util.Map;

import fi.zalando.core.utils.Preconditions;
import timber.log.Timber;

/**
 * Thread safe class that stores key/value primitive data in the persistent storage
 *
 * Created by jduran on 23/11/15.
 */
public class PersistentHashTable {

    private final Context context;
    private SharedPreferences sharedPreferences;
    private String persistentHashTableId;

    public PersistentHashTable(Context applicationContext) {

        context = applicationContext.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Returns the {@link Boolean} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Boolean} default value if the key doesn't exist
     * @return {@link Boolean} of the given key
     */
    public synchronized Boolean get(@NonNull String key, boolean defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Saves in the Persistent table the given {@link Boolean} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Boolean} value
     * @param keyValue {@link Boolean} value to link the key
     */
    public synchronized void put(@NonNull String key, boolean keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putBoolean(key, keyValue).apply();
    }

    /**
     * Returns the {@link String} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link String} default value if the key doesn't exist
     * @return {@link String} of the given key
     */
    public String get(@NonNull String key, String defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Saves in the Persistent table the given {@link String} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link String} value
     * @param keyValue {@link String} value to link the key
     */
    public void put(@NonNull String key, String keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putString(key, keyValue).apply();
    }

    /**
     * Returns the {@link Long} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Long} default value if the key doesn't exist
     * @return {@link Long} of the given key
     */
    public Long get(@NonNull String key, Long defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getLong(key, defaultValue);
    }

    /**
     * Saves in the Persistent table the given {@link Long} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Long} value
     * @param keyValue {@link Long} value to link the key
     */
    public void put(@NonNull String key, Long keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putLong(key, keyValue).apply();
    }

    /**
     * Returns the {@link Integer} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Integer} default value if the key doesn't exist
     * @return {@link Integer} of the given key
     */
    public Integer get(@NonNull String key, Integer defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Saves in the Persistent table the given {@link Integer} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Integer} value
     * @param keyValue {@link Integer} value to link the key
     */
    public void put(@NonNull String key, Integer keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putInt(key, keyValue).apply();
    }

    /**
     * Returns the {@link Date} value of the given {@link String} key
     *
     * @param key          {@link String} with the key
     * @param defaultValue {@link Date} default value if the key doesn't exist
     * @return {@link Date} of the given key
     */
    public Date get(@NonNull String key, Date defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return new Date(sharedPreferences.getLong(key, defaultValue.getTime()));
    }

    /**
     * Saves in the Persistent table the given {@link Date} for the given {@link String} key
     *
     * @param key      {@link String} key to link the {@link Date} value
     * @param keyValue {@link Date} value to link the key
     */
    public void put(@NonNull String key, Date keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putLong(key, keyValue.getTime()).apply();
    }

    /**
     * Saves in the Persistent table the objects allocated in the given {@link Map} using the map
     * key as key and value as value in the {@link PersistentHashTable}
     *
     * @param values {@link Map} with the values to store
     */
    public void put(@NonNull Map<String, Object> values) {

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String key : values.keySet()) {

            // Get the content value from the map
            final Object contentValue = values.get(key);

            // Add each supported item
            if (contentValue instanceof Boolean) {
                editor.putBoolean(key, (Boolean) contentValue);
                continue;
            }
            if (contentValue instanceof String) {
                editor.putString(key, (String) contentValue);
                continue;
            }
            if (contentValue instanceof Long) {
                editor.putLong(key, (Long) contentValue);
                continue;
            }
            if (contentValue instanceof Date) {
                editor.putLong(key, ((Date) contentValue).getTime());
                continue;
            }
            if (contentValue instanceof Integer) {
                editor.putInt(key, (Integer) contentValue);
                continue;
            }

            // If reached this point we are trying to add a not supported type.
            // Throw exception
            throw new IllegalArgumentException(String.format("Saving %s type is not supported",
                    contentValue.getClass()));
        }

        editor.apply();
    }

    /**
     * Clears the {@link PersistentHashTable}
     */
    public void reset() {

        Timber.w("reset: " + (TextUtils.isEmpty(persistentHashTableId) ? "Default" :
                persistentHashTableId));

        sharedPreferences.edit().clear().apply();
    }

    /**
     * Checks if the persistent hash table is empty
     *
     * @return {@link Boolean} indicating if the table is empty
     */
    public boolean isEmpty() {

        return sharedPreferences.getAll().isEmpty();
    }

    /**
     * Switches the {@link PersistentHashTable} to the given Id. It creates the table if it does not
     * exist
     *
     * @param persistentHashTableId {@link String} with the id of the persistent table to switch to
     */
    public synchronized void switchStorage(@NonNull String persistentHashTableId) {

        // Check preconditions
        Preconditions.checkArgument(!TextUtils.isEmpty(persistentHashTableId), "Provided " +
                "HashTableId is empty");

        Timber.w("switchStorage: " + persistentHashTableId);

        this.persistentHashTableId = persistentHashTableId;
        sharedPreferences = context.getSharedPreferences(this.persistentHashTableId, Context
                .MODE_PRIVATE);
    }

    /**
     * Checks if the given key is not empty
     *
     * @param key {@link String} to validate
     */
    private void checkNonEmptyKeyPrecondition(String key) {

        Preconditions.checkArgument(!TextUtils.isEmpty(key), "Empty key is not supported");
    }

}
