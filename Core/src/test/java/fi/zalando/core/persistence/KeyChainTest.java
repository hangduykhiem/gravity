package fi.zalando.core.persistence;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.zalando.core.BuildConfig;
import fi.zalando.core.persistence.impl.SharedPreferencesPersistentHashTableImpl;
import fi.zalando.core.utils.SecurityUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test class for {@link SecurityUtils} utility class
 *
 * Created by jduran on 12/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest =
        "src/main/AndroidManifest.xml")
public class KeyChainTest {

    @Test
    public void testSwitchStorageCalled() {

        // Mock the persistent hash table
        PersistentHashTable mockedPersistentHashTable = mock(PersistentHashTable.class);
        // Call constructor to force switch storage to be called
        new KeyChain(mockedPersistentHashTable);
        // verify switch storage called
        verify(mockedPersistentHashTable, times(1)).switchStorage(anyString());
    }

    @Test
    public void testSaveAndGetSingleItem() {

        KeyChain keyChain = new KeyChain(new SharedPreferencesPersistentHashTableImpl
                (RuntimeEnvironment.application));

        String keyToUse = "key";
        String valueToStore = "value";

        // First, store a key value pair
        keyChain.put(keyToUse, valueToStore);

        // Compare that saved data is the same as retrieved one
        assertEquals(keyChain.get(keyToUse), valueToStore);

        // Check that observable loading also works
        assertEquals(keyChain.load(keyToUse).toBlocking().first(), valueToStore);

        // verify null returned when key was not saved
        assertNull(keyChain.get("anotherKey"));

        // Test reset works
        keyChain.reset();

        // Now the key should return null
        assertNull(keyChain.get(keyToUse));
    }

    @Test
    public void testSaveAndGetMultipleItems() {

        KeyChain keyChain = new KeyChain(new SharedPreferencesPersistentHashTableImpl
                (RuntimeEnvironment.application));

        String keyToUse = "key";
        String valueToStore = "value";

        String keyToUse2 = "key2";
        String valueToStore2 = "value2";

        // Create a map to add them at the same time
        Map<String, String> toAddMap = new HashMap<>();
        toAddMap.put(keyToUse, valueToStore);
        toAddMap.put(keyToUse2, valueToStore2);

        // Add items to keychain
        keyChain.put(toAddMap);

        // Load the items again all of them at the same time
        List<String> keysToRetrieve = new ArrayList<>();
        keysToRetrieve.add(keyToUse);
        keysToRetrieve.add(keyToUse2);

        // Retrieve the items
        Map<String, String> retrievedMap = keyChain.get(keysToRetrieve);

        // Verify both keys were loaded
        assertEquals(retrievedMap.get(keyToUse), valueToStore);
        assertEquals(retrievedMap.get(keyToUse2), valueToStore2);
        // verify that a non existent item returns null
        assertNull(retrievedMap.get("another key"));

        // verify load also works
        retrievedMap = keyChain.load(keysToRetrieve).toBlocking().first();

        // Verify both keys were loaded
        assertEquals(retrievedMap.get(keyToUse), valueToStore);
        assertEquals(retrievedMap.get(keyToUse2), valueToStore2);
        // verify that a non existent item returns null
        assertNull(retrievedMap.get("another key"));
    }
}
