package fi.zalando.core.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fi.zalando.core.utils.SecurityUtils;
import rx.Observable;
import timber.log.Timber;

/**
 * Class that stores data in a persistent hash table in a secure way
 *
 * @author jduran
 */
public class KeyChain {

    private final PersistentHashTable persistentHashTable;

    /**
     * Constructor
     *
     * @param persistentHashTable {@link PersistentHashTable} to use internally
     */
    public KeyChain(PersistentHashTable persistentHashTable) {

        this.persistentHashTable = persistentHashTable;

        try {
            initKeyChain();
        } catch (Exception ignore) {
        }
    }

    /**
     * Inits the {@link KeyChain}
     *
     * @throws GeneralSecurityException     if AES is not implemented on this system
     * @throws UnsupportedEncodingException if UTF-8 is not supported in this system
     */
    private void initKeyChain() throws GeneralSecurityException, UnsupportedEncodingException {

        // switch to an own storage in the persistent storage
        persistentHashTable.switchStorage(KeyChain.class.getName());

        // Check if the keychain has been initialised
        if (persistentHashTable.isEmpty()) {

            // Create a map to store about 1000 elements to the persistent hash table to make
            // search of key more difficult to hack
            Map<String, Object> toAddItems = new HashMap<>();
            Random random = new Random(System.currentTimeMillis());
            int amountOfItems = random.nextInt(100);
            for (int i = 0; i <= amountOfItems; i++) {
                // Add randomly key encrypts or value encrypts
                if (random.nextBoolean()) {
                    toAddItems.put(SecurityUtils.generateRandomKey(), SecurityUtils
                            .generateRandomKey());
                } else {
                    toAddItems.put(SecurityUtils.encryptKey(SecurityUtils
                            .generateRandomString()), SecurityUtils.generateRandomKey());
                }
            }
            // Save all the items in the persistent hash
            persistentHashTable.put(toAddItems);
        }
    }

    /**
     * Provides the saved {@link String} that was securely saved on the KeyChain
     *
     * @param key {@link String} with the key to extract the item
     * @return {@link String} that was saved before. Null if doesn't exist.
     */
    @Nullable
    public String get(@NonNull String key) {

        // Get what is the encrypted key so we know which key was used to decrypt the real value
        String encryptedKey;
        String encryptionPasswordUsed;
        String encryptedValue;

        // If doesn't exist, means that it wasn't saved beforehand
        encryptedKey = SecurityUtils.encryptKey(key);
        encryptionPasswordUsed = persistentHashTable.get(encryptedKey, "");
        if (!TextUtils.isEmpty(encryptionPasswordUsed)) {
            encryptedValue = persistentHashTable.get(encryptionPasswordUsed, "");
            if (!TextUtils.isEmpty(encryptedValue)) {
                try {
                    return SecurityUtils.decrypt(encryptedValue, encryptionPasswordUsed);
                } catch (Exception e) {
                    Timber.e(e, "Failed to decrypt value");
                    // If getting exception here, the key was also saved using plain text
                    return encryptedValue;
                }
            }
        }
        // If code reach this point, means that nothing was found with that key
        return null;
    }

    /**
     * Provides the saved {@link String}s that were securely saved on the KeyChain
     *
     * @param keys {@link List} of {@link String} with the keys to extract
     * @return {@link Map} with the extracted values matching the keys
     */
    public Map<String, String> get(@NonNull List<String> keys) {

        Map<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            resultMap.put(keys.get(i), get(keys.get(i)));
        }
        return resultMap;
    }

    /**
     * Provides an {@link Observable} that loads the value related to the given {@link String} key
     *
     * @param key {@link String} with the key to load the value
     * @return {@link Observable} that loads the {@link String} matching the given key
     */
    public Observable<String> load(@NonNull String key) {

        return Observable.just(get(key));
    }

    /**
     * Provides an {@link Observable} that loads the value related to the given {@link List} of
     * {@link String} keys
     *
     * @param keys {@link List} of {@link String}s with the key to load the values
     * @return {@link Observable} that loads the {@link List} {@link String} matching the given key
     */
    public Observable<Map<String, String>> load(@NonNull List<String> keys) {

        return Observable.just(get(keys));
    }

    /**
     * Stores securely the given key/value {@link String}s
     *
     * @param key   {@link String} with the key
     * @param value {@link String} with the value
     */
    public void put(@NonNull String key, String value) {

        Pair<String, String> encryptedKeyValue;

        String encryptedKey = SecurityUtils.encryptKey(key);
        try {
            // Encrypt the keyValue and save the used key from the tuple
            encryptedKeyValue = SecurityUtils.encrypt(value);
        } catch (Exception e) {
            Timber.e(e, "Failed to encrypt value");
            // If fails (extremely rare case), use plain texts
            encryptedKeyValue = new Pair<>(value, SecurityUtils.generateRandomString());
        }
        // Add to the persistent hash table the following items
        // <encryptedKey, UsedKeyWhenEncryptingValue>
        // <UsedKeyWhenEncryptingValue, EncryptedValue>
        persistentHashTable.put(encryptedKey, encryptedKeyValue.second);
        persistentHashTable.put(encryptedKeyValue.second, encryptedKeyValue.first);
    }

    /**
     * Stores the given {@link Map} of key/value {@link String}s securely
     *
     * @param values {@link Map} with the values to save
     */
    public void put(@NonNull Map<String, String> values) {

        // Make a loop that reuses the complex put method
        // Iterate over the keys
        for (String key : values.keySet()) {
            // Add the key/value pair to keychain
            put(key, values.get(key));
        }
    }

    /**
     * Clears the keychain
     */
    public void reset() {

        persistentHashTable.reset();
    }

}
