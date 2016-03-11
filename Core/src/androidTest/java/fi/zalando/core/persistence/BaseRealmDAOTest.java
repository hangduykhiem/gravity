package fi.zalando.core.persistence;

import android.app.Application;
import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fi.zalando.core.persistence.mocks.MockValidRealmModel;
import fi.zalando.core.persistence.mocks.MockValidWithoutPrimaryKeyRealmModel;
import fi.zalando.core.persistence.mocks.MockWrongPrimaryKeyRealmModel;
import fi.zalando.core.persistence.mocks.MockWrongRealmIdRealmModel;
import io.realm.RealmConfiguration;

/**
 * Test class for {@link BaseRealmDAO}
 *
 * Created by jduran on 07/03/16.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class BaseRealmDAOTest extends ApplicationTestCase<Application> {

    public BaseRealmDAOTest() {
        super(Application.class);
    }

    private RealmConfiguration realmConfiguration;

    @Before
    public void setUp() {

        realmConfiguration = new RealmConfiguration.Builder(getContext())
                .deleteRealmIfMigrationNeeded().build();
    }


    @Test
    public void testWrongPrimaryKey() {

        // The check is done in the constructor, so WrongPrimaryKeyRealmModelDAO
        // should crash right away
        try {
            new WrongPrimaryKeyRealmModelDAO(realmConfiguration, MockWrongPrimaryKeyRealmModel.class,
                    EventBus.getDefault());
        } catch (IllegalStateException exception) {
            assertTrue(exception.getMessage().contains("WrongPrimaryKeyRealmModel"));
        }
    }

    @Test
    public void testWrongRealmId() {

        // The check is done in the constructor, so WrongPrimaryKeyRealmModelDAO
        // should crash right away
        try {
            new WrongRealmIdRealmModelDAO(realmConfiguration, MockWrongRealmIdRealmModel.class,
                    EventBus.getDefault());
        } catch (IllegalStateException exception) {
            assertTrue(exception.getMessage().contains("@RealmId"));
        }
    }

    @Test
    public void testValidWithoutPrimaryKeyRealmModel() {

        ValidWithoutPrimaryKeyRealmModelDAO validWithoutPrimaryKeyRealmModelDAO = new
                ValidWithoutPrimaryKeyRealmModelDAO(realmConfiguration,
                MockValidWithoutPrimaryKeyRealmModel.class, EventBus.getDefault());
        // Clean it first
        validWithoutPrimaryKeyRealmModelDAO.clean();
        // should be empty
        assertTrue(validWithoutPrimaryKeyRealmModelDAO.isEmpty());
        // add an item
        validWithoutPrimaryKeyRealmModelDAO.save(new MockValidWithoutPrimaryKeyRealmModel());
        // check it was inserted
        assertEquals(validWithoutPrimaryKeyRealmModelDAO.count(), 1);
        // add an item
        validWithoutPrimaryKeyRealmModelDAO.save(new MockValidWithoutPrimaryKeyRealmModel());
        // check it was inserted
        assertEquals(validWithoutPrimaryKeyRealmModelDAO.count(), 2);
    }

    @Test
    public void testIsEmpty() {

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertTrue(validRealmModelDAO.isEmpty());
        // add an item
        validRealmModelDAO.save(new MockValidRealmModel("StringId"));
        // now it must have something
        assertFalse(validRealmModelDAO.isEmpty());
    }

    @Test
    public void testClean() {

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertTrue(validRealmModelDAO.isEmpty());
        // add an item
        validRealmModelDAO.save(new MockValidRealmModel("StringId"));
        // now it must have something
        assertFalse(validRealmModelDAO.isEmpty());
        // now clean it again
        validRealmModelDAO.clean();
        // should be empty again
        assertTrue(validRealmModelDAO.isEmpty());
    }

    @Test
    public void testCount() {

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new MockValidRealmModel("StringId"));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
    }

    @Test
    public void testDelete() {

        // Mock id
        String id = "StringId";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new MockValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
        // delete using the created id
        validRealmModelDAO.delete(id);
        // not it should be empty again
        assertTrue(validRealmModelDAO.isEmpty());
    }

    @Test
    public void testSaveSingle() {

        // Mock id
        String id = "StringId";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new MockValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
        // now load it and see that the items is the same
        assertEquals(validRealmModelDAO.loadById(id).toBlocking().first().getId(), id);
        // Test also if we save it again, size should be one since the object has same id
        validRealmModelDAO.save(new MockValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
    }

    @Test
    public void testSaveMultiple() {

        // Mock id
        String id = "StringId";
        String id2 = "StringId2";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // Create a list to add later the items
        List<MockValidRealmModel> itemsToAdd = new ArrayList<>();
        itemsToAdd.add(new MockValidRealmModel(id));
        itemsToAdd.add(new MockValidRealmModel(id2));
        // save the items
        validRealmModelDAO.save(itemsToAdd);
        // size must be 2
        assertEquals(validRealmModelDAO.count(), 2);
        // now, add items with same id to see if the size is still two
        for (int i = 0; i < 100; i++) {
            itemsToAdd.add(new MockValidRealmModel(id));
            itemsToAdd.add(new MockValidRealmModel(id2));
        }
        // save the items
        validRealmModelDAO.save(itemsToAdd);
        // size still must be 2
        assertEquals(validRealmModelDAO.count(), 2);
        // Load all
        List<MockValidRealmModel> allItems = validRealmModelDAO.loadAll().toBlocking().first();
        // now check that the items are there
        for (MockValidRealmModel mockValidRealmModel : allItems) {
            assertTrue(mockValidRealmModel.getId().equals(id) || mockValidRealmModel.getId().equals(id2));
        }
    }

    @Test
    public void testReplace() {

        // Mock id
        String id = "StringId";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new MockValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
        // now load it and see that the items is the same
        assertEquals(validRealmModelDAO.loadById(id).toBlocking().first().getId(), id);
        // Test also if we save it again, size should be one since the object has same id
        validRealmModelDAO.save(new MockValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
        // Mock more ids
        String anotherId = "StringIdAnother";
        String anotherId2 = "StringIdAnother2";
        // Create a list to replace
        List<MockValidRealmModel> itemsToReplace = new ArrayList<>();
        itemsToReplace.add(new MockValidRealmModel(anotherId));
        itemsToReplace.add(new MockValidRealmModel(anotherId2));
        // Replace the items
        validRealmModelDAO.replace(itemsToReplace);
        // size should be the same as the list
        assertEquals(itemsToReplace.size(), validRealmModelDAO.count());
        // Check that new ids are there, but not the old one

        // Load all
        List<MockValidRealmModel> allItems = validRealmModelDAO.loadAll().toBlocking().first();
        // now check that the items are there
        for (MockValidRealmModel mockValidRealmModel : allItems) {
            assertTrue(mockValidRealmModel.getId().equals(anotherId) || mockValidRealmModel.getId()
                    .equals(anotherId2));
            assertTrue(!mockValidRealmModel.getId().equals(id));
        }
    }

    @Test
    public void testHasExpired() {

        // Mock id
        String id = "StringId";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                MockValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // add an item
        validRealmModelDAO.save(new MockValidRealmModel(id));
        // now load it again
        MockValidRealmModel loadedMockValidRealmModel = validRealmModelDAO.loadById(id).toBlocking()
                .first();
        // now verify it is not expired
        assertFalse(validRealmModelDAO.hasExpired(loadedMockValidRealmModel, 1, TimeUnit.DAYS));
        // wait for some milliseconds
        SystemClock.sleep(101);
        // now verify it is expired
        assertTrue(validRealmModelDAO.hasExpired(loadedMockValidRealmModel, 100, TimeUnit
                .MILLISECONDS));
    }

    private class WrongRealmIdRealmModelDAO extends BaseRealmDAO<MockWrongRealmIdRealmModel> {

        protected WrongRealmIdRealmModelDAO(RealmConfiguration realmConfiguration,
                                            Class<MockWrongRealmIdRealmModel> clazz, EventBus
                                                    eventBus) {
            super(realmConfiguration, clazz, eventBus);
        }
    }

    private class WrongPrimaryKeyRealmModelDAO extends BaseRealmDAO<MockWrongPrimaryKeyRealmModel> {

        protected WrongPrimaryKeyRealmModelDAO(RealmConfiguration realmConfiguration,
                                               Class<MockWrongPrimaryKeyRealmModel> clazz, EventBus
                                                       eventBus) {
            super(realmConfiguration, clazz, eventBus);
        }
    }

    private class ValidWithoutPrimaryKeyRealmModelDAO extends
            BaseRealmDAO<MockValidWithoutPrimaryKeyRealmModel> {

        protected ValidWithoutPrimaryKeyRealmModelDAO(RealmConfiguration realmConfiguration,
                                                      Class<MockValidWithoutPrimaryKeyRealmModel>
                                                              clazz, EventBus eventBus) {

            super(realmConfiguration, clazz, eventBus);
        }
    }

    private class ValidRealmModelDAO extends BaseRealmDAO<MockValidRealmModel> {

        protected ValidRealmModelDAO(RealmConfiguration realmConfiguration,
                                     Class<MockValidRealmModel> clazz, EventBus eventBus) {

            super(realmConfiguration, clazz, eventBus);
        }
    }

}
