package fi.zalando.core.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import fi.zalando.core.utils.Preconditions;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import java.util.Date;
import java.util.Map;
import javax.inject.Inject;
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

  @Inject
  public PersistentHashTable(Context applicationContext) {

    context = applicationContext.getApplicationContext();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  /**
   * Provides the saved {@link T} value of the given {@link Class} <T>. If key/value relation does
   * not exist, provide default value
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link T} with the default value in case key does not exist
   * @param clazz {@link Class} of the datatype to retrieve
   * @param <T> {@link T} type to retrieve
   * @return {@link T} matching the given key
   */
  private <T> T get(String key, T defaultValue, Class<T> clazz) {

    Preconditions.checkArgument(defaultValue.getClass().equals(clazz));

    // Check each supported type
    if (defaultValue instanceof Boolean) {
      return clazz.cast(sharedPreferences.getBoolean(validateAndCleanKey(key),
          (Boolean) defaultValue));
    }
    if (defaultValue instanceof String) {
      return clazz.cast(sharedPreferences.getString(validateAndCleanKey(key),
          (String) defaultValue));
    }
    if (defaultValue instanceof Float) {
      return clazz.cast(sharedPreferences.getFloat(validateAndCleanKey(key),
          (Float) defaultValue));
    }
    if (defaultValue instanceof Long) {
      return clazz.cast(sharedPreferences.getLong(validateAndCleanKey(key),
          (Long) defaultValue));
    }
    if (defaultValue instanceof Date) {
      return clazz.cast(new Date(sharedPreferences.getLong(validateAndCleanKey(key),
          ((Date) defaultValue).getTime())));
    }
    if (defaultValue instanceof Integer) {
      return clazz.cast(sharedPreferences.getInt(validateAndCleanKey(key),
          (Integer) defaultValue));
    }

    // If reached this point we are trying to get a not supported type. Throw exception
    throw new IllegalArgumentException(String.format("Retrieving %s type is not supported",
        clazz));
  }

  /**
   * Returns the {@link Boolean} value of the given {@link String} key
   *
   * @param key {@link String} with the key
   * @param defaultValue {@link Boolean} default value if the key doesn't exist
   * @return {@link Boolean} of the given key
   */
  public synchronized Boolean get(@NonNull String key, boolean defaultValue) {

    return get(key, defaultValue, Boolean.class);
  }

  /**
   * Returns the {@link String} value of the given {@link String} key
   *
   * @param key {@link String} with the key
   * @param defaultValue {@link String} default value if the key doesn't exist
   * @return {@link String} of the given key
   */
  public String get(@NonNull String key, String defaultValue) {

    return get(key, defaultValue, String.class);
  }

  /**
   * Returns the {@link Long} value of the given {@link String} key
   *
   * @param key {@link String} with the key
   * @param defaultValue {@link Long} default value if the key doesn't exist
   * @return {@link Long} of the given key
   */
  public Long get(@NonNull String key, Long defaultValue) {

    return get(key, defaultValue, Long.class);
  }

  /**
   * Returns the {@link Float} value of the given {@link String} key
   *
   * @param key {@link String} with the key
   * @param defaultValue {@link Float} default value if the key doesn't exist
   * @return {@link Float} of the given key
   */
  public Float get(@NonNull String key, Float defaultValue) {

    return get(key, defaultValue, Float.class);
  }

  /**
   * Returns the {@link Integer} value of the given {@link String} key
   *
   * @param key {@link String} with the key
   * @param defaultValue {@link Integer} default value if the key doesn't exist
   * @return {@link Integer} of the given key
   */
  public Integer get(@NonNull String key, Integer defaultValue) {

    return get(key, defaultValue, Integer.class);
  }

  /**
   * Returns the {@link Date} value of the given {@link String} key
   *
   * @param key {@link String} with the key
   * @param defaultValue {@link Date} default value if the key doesn't exist
   * @return {@link Date} of the given key
   */
  public Date get(@NonNull String key, Date defaultValue) {

    return get(key, defaultValue, Date.class);
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
   * Loads the saved {@link T} value of the given {@link Class} <T> using an {@link Observable} . If
   * key/value relation does not exist, provide default value
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link T} with the default value in case key does not exist
   * @param clazz {@link Class} of the datatype to retrieve
   * @param <T> {@link T} type to retrieve
   * @return {@link Observable} to load the matching given key
   */
  private <T> Observable<T> load(String key, T defaultValue, Class<T> clazz) {

    return Observable.create(new ObservableOnSubscribe<T>() {
      @Override
      public void subscribe(ObservableEmitter<T> emitter) throws Exception {

        SharedPreferences.OnSharedPreferenceChangeListener
            preferenceChangeListener = (sharedPreferencesInstance, changedKey) -> {
          if (key.equals(changedKey)) {
            emitter.onNext(get(key, defaultValue, clazz));
          }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener
            (preferenceChangeListener);
        emitter.setCancellable(() ->
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener));

        emitter.onNext(get(key, defaultValue, clazz));
      }
      // Do not emit same items
    }).distinctUntilChanged();
  }

  /**
   * Loads using an {@link Observable} the {@link Boolean} stored with the given {@link String} key
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link Boolean} with the default value to return if the key does not exist
   * @return {@link Observable} to load the {@link Boolean} with the given key
   */
  public Observable<Boolean> load(String key, Boolean defaultValue) {

    return load(key, defaultValue, Boolean.class);
  }

  /**
   * Loads using an {@link Observable} the {@link String} stored with the given {@link String} key
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link String} with the default value to return if the key does not exist
   * @return {@link Observable} to load the {@link String} with the given key
   */
  public Observable<String> load(String key, String defaultValue) {

    return load(key, defaultValue, String.class);
  }

  /**
   * Loads using an {@link Observable} the {@link Float} stored with the given {@link String} key
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link Float} with the default value to return if the key does not exist
   * @return {@link Observable} to load the {@link Float} with the given key
   */
  public Observable<Float> load(String key, Float defaultValue) {

    return load(key, defaultValue, Float.class);
  }

  /**
   * Loads using an {@link Observable} the {@link Long} stored with the given {@link String} key
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link Long} with the default value to return if the key does not exist
   * @return {@link Observable} to load the {@link Long} with the given key
   */
  public Observable<Long> load(String key, Long defaultValue) {

    return load(key, defaultValue, Long.class);
  }

  /**
   * Loads using an {@link Observable} the {@link Date} stored with the given {@link String} key
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link Date} with the default value to return if the key does not exist
   * @return {@link Observable} to load the {@link Date} with the given key
   */
  public Observable<Date> load(String key, Date defaultValue) {

    return load(key, defaultValue, Date.class);
  }

  /**
   * Loads using an {@link Observable} the {@link Integer} stored with the given {@link String} key
   *
   * @param key {@link String} with the key to retrieve
   * @param defaultValue {@link Integer} with the default value to return if the key does not exist
   * @return {@link Observable} to load the {@link Integer} with the given key
   */
  public Observable<Integer> load(String key, Integer defaultValue) {

    return load(key, defaultValue, Integer.class);
  }

  /**
   * Puts a new value in the persistent hash table using the given {@link String} key
   *
   * @param key {@link String} with the key to save
   * @param value {@link T} with the value to save
   * @param <T> {@link T} class to save
   */
  public <T> void put(String key, T value) {

    // Check each supported type
    if (value instanceof Boolean) {
      sharedPreferences.edit().putBoolean(validateAndCleanKey(key), (Boolean) value).apply();
      return;
    }
    if (value instanceof String) {
      sharedPreferences.edit().putString(validateAndCleanKey(key), (String) value).apply();
      return;
    }
    if (value instanceof Float) {
      sharedPreferences.edit().putFloat(validateAndCleanKey(key), (Float) value).apply();
      return;
    }
    if (value instanceof Long) {
      sharedPreferences.edit().putLong(validateAndCleanKey(key), (Long) value).apply();
      return;
    }
    if (value instanceof Date) {
      sharedPreferences.edit().putLong(validateAndCleanKey(key), ((Date) value).getTime())
          .apply();
      return;
    }
    if (value instanceof Integer) {
      sharedPreferences.edit().putInt(validateAndCleanKey(key), (Integer) value).apply();
      return;
    }

    // If reached this point we are trying to get a not supported type. Throw exception
    throw new IllegalArgumentException(String.format("Saving %s type is not supported",
        value.getClass()));
  }

  /**
   * Saves in the Persistent table the objects allocated in the given {@link Map} using the map key
   * as key and value as value in the {@link PersistentHashTable}
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
        editor.putBoolean(validateAndCleanKey(key), (Boolean) contentValue);
        continue;
      }
      if (contentValue instanceof String) {
        editor.putString(validateAndCleanKey(key), ((String) contentValue).trim());
        continue;
      }
      if (contentValue instanceof Float) {
        editor.putFloat(validateAndCleanKey(key), (Float) contentValue);
        continue;
      }
      if (contentValue instanceof Long) {
        editor.putLong(validateAndCleanKey(key), (Long) contentValue);
        continue;
      }
      if (contentValue instanceof Date) {
        editor.putLong(validateAndCleanKey(key), ((Date) contentValue).getTime());
        continue;
      }
      if (contentValue instanceof Integer) {
        editor.putInt(validateAndCleanKey(key), (Integer) contentValue);
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

    Timber.w("reset: %s", (TextUtils.isEmpty(persistentHashTableId) ? "Default" :
        persistentHashTableId));

    sharedPreferences.edit().clear().apply();
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

    Timber.w("switchStorage: %s", persistentHashTableId);

    this.persistentHashTableId = persistentHashTableId;
    sharedPreferences = context.getSharedPreferences(this.persistentHashTableId, Context
        .MODE_PRIVATE);
  }

  /**
   * Checks if the given key is not empty and returns a clean {@link String} key that shared
   * preferences is able to save
   *
   * @param key {@link String} to validate
   * @return {@link String} with cleaned key that shared preferences is able to save
   */
  private String validateAndCleanKey(String key) {

    // Remove empty spaces and break lines from beginning and end of lines
    // SharedPreferences has a bug -> http://bit.ly/1ooQ7wE
    String keyToUse = key.trim();
    Preconditions.checkArgument(!TextUtils.isEmpty(keyToUse), String.format("%s key is not " +
        "supported", key));
    return keyToUse;
  }

}
