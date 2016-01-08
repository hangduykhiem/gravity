package fi.zalando.core.persistence.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import fi.zalando.core.persistence.PersistentHashTable;
import fi.zalando.core.utils.Preconditions;
import timber.log.Timber;

/**
 * Implementation of {@link PersistentHashTable} using {@link android.content.SharedPreferences} as
 * content storage
 *
 * Created by jduran on 23/11/15.
 */
public class SharedPreferencesPersistentHashTableImpl implements PersistentHashTable {

    private final Context context;
    private SharedPreferences sharedPreferences;
    private String persistentHashTableId;

    @Inject
    public SharedPreferencesPersistentHashTableImpl(Context applicationContext) {

        context = applicationContext.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public synchronized Boolean get(@NonNull String key, boolean defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public synchronized void put(@NonNull String key, boolean keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putBoolean(key, keyValue).apply();
    }

    @Override
    public String get(@NonNull String key, String defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void put(@NonNull String key, String keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putString(key, keyValue).apply();
    }

    @Override
    public Long get(@NonNull String key, Long defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getLong(key, defaultValue);
    }

    @Override
    public void put(@NonNull String key, Long keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putLong(key, keyValue).apply();
    }

    @Override
    public Integer get(@NonNull String key, Integer defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return sharedPreferences.getInt(key, defaultValue);
    }

    @Override
    public void put(@NonNull String key, Integer keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putInt(key, keyValue).apply();
    }

    @Override
    public Date get(@NonNull String key, Date defaultValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        return new Date(sharedPreferences.getLong(key, defaultValue.getTime()));
    }

    @Override
    public void put(@NonNull String key, Date keyValue) {

        // Check preconditions
        checkNonEmptyKeyPrecondition(key);

        sharedPreferences.edit().putLong(key, keyValue.getTime()).apply();
    }

    @Override
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

    @Override
    public void reset() {

        Timber.w("reset: " + (TextUtils.isEmpty(persistentHashTableId) ? "Default" :
                persistentHashTableId));

        sharedPreferences.edit().clear().apply();
    }

    @Override
    public synchronized void switchStorage(@NonNull String persistentHashTableId) {

        // Check preconditions
        Preconditions.checkArgument(!TextUtils.isEmpty(persistentHashTableId), "Provided " +
                "HashTableId is empty");

        Timber.w("switchStorage: " + persistentHashTableId);

        this.persistentHashTableId = persistentHashTableId;
        sharedPreferences = context.getSharedPreferences(this.persistentHashTableId, Context.MODE_PRIVATE);
    }

    private void checkNonEmptyKeyPrecondition(String key) {

        Preconditions.checkArgument(!TextUtils.isEmpty(key), "Empty key is not supported");
    }
}
