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

import fi.zalando.core.persistence.mocks.ValidRealmModel;
import fi.zalando.core.persistence.mocks.ValidWithoutPrimaryKeyRealmModel;
import fi.zalando.core.persistence.mocks.WrongPrimaryKeyRealmModel;
import fi.zalando.core.persistence.mocks.WrongRealmIdRealmModel;
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
            new WrongPrimaryKeyRealmModelDAO(realmConfiguration, WrongPrimaryKeyRealmModel.class,
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
            new WrongRealmIdRealmModelDAO(realmConfiguration, WrongRealmIdRealmModel.class,
                    EventBus.getDefault());
        } catch (IllegalStateException exception) {
            assertTrue(exception.getMessage().contains("@RealmId"));
        }
    }

    @Test
    public void testValidWithoutPrimaryKeyRealmModel() {

        ValidWithoutPrimaryKeyRealmModelDAO validWithoutPrimaryKeyRealmModelDAO = new
                ValidWithoutPrimaryKeyRealmModelDAO(realmConfiguration,
                ValidWithoutPrimaryKeyRealmModel.class, EventBus.getDefault());
        // should be empty
        assertTrue(validWithoutPrimaryKeyRealmModelDAO.isEmpty());
        // add an item
        validWithoutPrimaryKeyRealmModelDAO.save(new ValidWithoutPrimaryKeyRealmModel());
        // check it was inserted
        assertEquals(validWithoutPrimaryKeyRealmModelDAO.count(), 1);
        // add an item
        validWithoutPrimaryKeyRealmModelDAO.save(new ValidWithoutPrimaryKeyRealmModel());
        // check it was inserted
        assertEquals(validWithoutPrimaryKeyRealmModelDAO.count(), 2);
    }

    @Test
    public void testIsEmpty() {

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                ValidRealmModel.class, EventBus.getDefault());
        // Just created, should be empty
        assertTrue(validRealmModelDAO.isEmpty());
        // add an item
        validRealmModelDAO.save(new ValidRealmModel("StringId"));
        // now it must have something
        assertFalse(validRealmModelDAO.isEmpty());
    }

    @Test
    public void testClean() {

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                ValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertTrue(validRealmModelDAO.isEmpty());
        // add an item
        validRealmModelDAO.save(new ValidRealmModel("StringId"));
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
                ValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new ValidRealmModel("StringId"));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
    }

    @Test
    public void testDelete() {

        // Mock id
        String id = "StringId";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                ValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new ValidRealmModel(id));
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
                ValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new ValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
        // now load it and see that the items is the same
        assertEquals(validRealmModelDAO.loadById(id).toBlocking().first().getId(), id);
        // Test also if we save it again, size should be one since the object has same id
        validRealmModelDAO.save(new ValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
    }

    @Test
    public void testSaveMultiple() {

        // Mock id
        String id = "StringId";
        String id2 = "StringId2";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                ValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // Create a list to add later the items
        List<ValidRealmModel> itemsToAdd = new ArrayList<>();
        itemsToAdd.add(new ValidRealmModel(id));
        itemsToAdd.add(new ValidRealmModel(id2));
        // save the items
        validRealmModelDAO.save(itemsToAdd);
        // size must be 2
        assertEquals(validRealmModelDAO.count(), 2);
        // now, add items with same id to see if the size is still two
        for (int i = 0; i < 100; i++) {
            itemsToAdd.add(new ValidRealmModel(id));
            itemsToAdd.add(new ValidRealmModel(id2));
        }
        // save the items
        validRealmModelDAO.save(itemsToAdd);
        // size still must be 2
        assertEquals(validRealmModelDAO.count(), 2);
        // Load all
        List<ValidRealmModel> allItems = validRealmModelDAO.loadAll().toBlocking().first();
        // now check that the items are there
        for (ValidRealmModel validRealmModel : allItems) {
            assertTrue(validRealmModel.getId().equals(id) || validRealmModel.getId().equals(id2));
        }
    }

    @Test
    public void testReplace() {

        // Mock id
        String id = "StringId";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                ValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // Just created, should be empty
        assertEquals(validRealmModelDAO.count(), 0);
        // add an item
        validRealmModelDAO.save(new ValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
        // now load it and see that the items is the same
        assertEquals(validRealmModelDAO.loadById(id).toBlocking().first().getId(), id);
        // Test also if we save it again, size should be one since the object has same id
        validRealmModelDAO.save(new ValidRealmModel(id));
        // now it must have something
        assertEquals(validRealmModelDAO.count(), 1);
        // Mock more ids
        String anotherId = "StringIdAnother";
        String anotherId2 = "StringIdAnother2";
        // Create a list to replace
        List<ValidRealmModel> itemsToReplace = new ArrayList<>();
        itemsToReplace.add(new ValidRealmModel(anotherId));
        itemsToReplace.add(new ValidRealmModel(anotherId2));
        // Replace the items
        validRealmModelDAO.replace(itemsToReplace);
        // size should be the same as the list
        assertEquals(itemsToReplace.size(), validRealmModelDAO.count());
        // Check that new ids are there, but not the old one

        // Load all
        List<ValidRealmModel> allItems = validRealmModelDAO.loadAll().toBlocking().first();
        // now check that the items are there
        for (ValidRealmModel validRealmModel : allItems) {
            assertTrue(validRealmModel.getId().equals(anotherId) || validRealmModel.getId()
                    .equals(anotherId2));
            assertTrue(!validRealmModel.getId().equals(id));
        }
    }

    @Test
    public void testHasExpired() {

        // Mock id
        String id = "StringId";

        ValidRealmModelDAO validRealmModelDAO = new ValidRealmModelDAO(realmConfiguration,
                ValidRealmModel.class, EventBus.getDefault());
        // Clean it first
        validRealmModelDAO.clean();
        // add an item
        validRealmModelDAO.save(new ValidRealmModel(id));
        // now load it again
        ValidRealmModel loadedValidRealmModel = validRealmModelDAO.loadById(id).toBlocking()
                .first();
        // now verify it is not expired
        assertFalse(validRealmModelDAO.hasExpired(loadedValidRealmModel, 1, TimeUnit.DAYS));
        // wait for some milliseconds
        SystemClock.sleep(101);
        // now verify it is expired
        assertTrue(validRealmModelDAO.hasExpired(loadedValidRealmModel, 100, TimeUnit
                .MILLISECONDS));
    }

    private class WrongRealmIdRealmModelDAO extends BaseRealmDAO<WrongRealmIdRealmModel> {

        protected WrongRealmIdRealmModelDAO(RealmConfiguration realmConfiguration,
                                            Class<WrongRealmIdRealmModel> clazz, EventBus
                                                    eventBus) {
            super(realmConfiguration, clazz, eventBus);
        }
    }

    private class WrongPrimaryKeyRealmModelDAO extends BaseRealmDAO<WrongPrimaryKeyRealmModel> {

        protected WrongPrimaryKeyRealmModelDAO(RealmConfiguration realmConfiguration,
                                               Class<WrongPrimaryKeyRealmModel> clazz, EventBus
                                                       eventBus) {
            super(realmConfiguration, clazz, eventBus);
        }
    }

    private class ValidWithoutPrimaryKeyRealmModelDAO extends
            BaseRealmDAO<ValidWithoutPrimaryKeyRealmModel> {

        protected ValidWithoutPrimaryKeyRealmModelDAO(RealmConfiguration realmConfiguration,
                                                      Class<ValidWithoutPrimaryKeyRealmModel>
                                                              clazz, EventBus eventBus) {

            super(realmConfiguration, clazz, eventBus);
        }
    }

    private class ValidRealmModelDAO extends BaseRealmDAO<ValidRealmModel> {

        protected ValidRealmModelDAO(RealmConfiguration realmConfiguration,
                                     Class<ValidRealmModel> clazz, EventBus eventBus) {

            super(realmConfiguration, clazz, eventBus);
        }
    }

}
