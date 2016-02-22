package fi.zalando.core.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import fi.zalando.core.persistence.mocks.MockedRealmObject;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test class for {@link BaseRealmDAO} class
 *
 * Created by jduran on 22/02/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Realm.class})
public class BaseRealmDAOTest {

    @Mock
    private RealmConfiguration realmConfiguration;
    @Mock
    private Realm realm;

    @Before
    public void setup() throws Exception {

        realmConfiguration = mock(RealmConfiguration.class);
        realm = PowerMockito.mock(Realm.class);
    }

    @Test
    public void testClear() {

        // Need to extend base class, not allowed to have abstract instances
        BaseRealmDAO<MockedRealmObject> baseRealmDAO = spy(new BaseRealmDAO<MockedRealmObject>
                (realmConfiguration, MockedRealmObject.class) {
            @Override
            public Realm getRealmInstance() {

                return realm;
            }
        });

        // Make request
        baseRealmDAO.clear();
        // Verify methods are called
        verify(realm, times(1)).clear(eq(MockedRealmObject.class));
        verify(realm, times(1)).close();
    }

    @Test
    public void testDeleteSingleModel() {

        // Need to extend base class, not allowed to have abstract instances
        BaseRealmDAO<MockedRealmObject> baseRealmDAO = spy(new BaseRealmDAO<MockedRealmObject>
                (realmConfiguration, MockedRealmObject.class) {
            @Override
            public Realm getRealmInstance() {

                return realm;
            }
        });

        MockedRealmObject mockedRealmObject = spy(new MockedRealmObject());

        // Do not execute real realm method
        doNothing().when(mockedRealmObject).removeFromRealm();

        // Make test request
        baseRealmDAO.delete(mockedRealmObject);
        // verify methods are called
        verify(realm, times(1)).beginTransaction();
        verify(realm, times(1)).commitTransaction();
        verify(mockedRealmObject, times(1)).removeFromRealm();
        verify(realm, times(1)).close();
    }

    @Test
    public void testDeleteIterable() {

        // Need to extend base class, not allowed to have abstract instances
        BaseRealmDAO<MockedRealmObject> baseRealmDAO = spy(new BaseRealmDAO<MockedRealmObject>
                (realmConfiguration, MockedRealmObject.class) {
            @Override
            public Realm getRealmInstance() {

                return realm;
            }
        });

        // Prepare mocked models
        MockedRealmObject mockedRealmObject = spy(new MockedRealmObject());
        MockedRealmObject mockedRealmObject2 = spy(new MockedRealmObject());

        List<MockedRealmObject> mockedRealmObjectList = new ArrayList<>();
        mockedRealmObjectList.add(mockedRealmObject);
        mockedRealmObjectList.add(mockedRealmObject2);

        // Do not execute real realm method
        doNothing().when(mockedRealmObject).removeFromRealm();
        doNothing().when(mockedRealmObject2).removeFromRealm();

        // Make test request
        baseRealmDAO.delete(mockedRealmObjectList);
        // verify methods are called
        verify(realm, times(1)).beginTransaction();
        verify(realm, times(1)).commitTransaction();
        verify(mockedRealmObject, times(1)).removeFromRealm();
        verify(mockedRealmObject2, times(1)).removeFromRealm();
        verify(realm, times(1)).close();
    }

    @Test
    public void saveSingleModelWithPrimaryKey() throws Exception {

        // Need to extend base class, not allowed to have abstract instances
        BaseRealmDAO<MockedRealmObject> baseRealmDAO = spy(new BaseRealmDAO<MockedRealmObject>
                (realmConfiguration, MockedRealmObject.class) {
            @Override
            public Realm getRealmInstance() {

                return realm;
            }

            @Override
            protected boolean hasPrimaryKey(Realm realm) {
                return true;
            }
        });

        MockedRealmObject mockedRealmObject = spy(new MockedRealmObject());

        // Make test request
        baseRealmDAO.save(mockedRealmObject);

        // verify methods are called
        verify(realm, times(1)).beginTransaction();
        verify(realm, times(1)).commitTransaction();
        verify(mockedRealmObject, times(1)).setSavedDate(anyLong());
        verify(realm, times(1)).copyToRealmOrUpdate(eq(mockedRealmObject));
        verify(realm, times(1)).close();
    }

    @Test
    public void saveSingleModelWithoutPrimaryKey() throws Exception {

        // Need to extend base class, not allowed to have abstract instances
        BaseRealmDAO<MockedRealmObject> baseRealmDAO = spy(new BaseRealmDAO<MockedRealmObject>
                (realmConfiguration, MockedRealmObject.class) {
            @Override
            public Realm getRealmInstance() {

                return realm;
            }

            @Override
            protected boolean hasPrimaryKey(Realm realm) {
                return false;
            }
        });

        MockedRealmObject mockedRealmObject = spy(new MockedRealmObject());

        // Make test request
        baseRealmDAO.save(mockedRealmObject);

        // verify methods are called
        verify(realm, times(1)).beginTransaction();
        verify(realm, times(1)).commitTransaction();
        verify(mockedRealmObject, times(1)).setSavedDate(anyLong());
        verify(realm, times(1)).copyToRealm(eq(mockedRealmObject));
        verify(realm, times(1)).close();
    }

    @Test
    public void saveMultipleModelsWithPrimaryKey() throws Exception {

        // Need to extend base class, not allowed to have abstract instances
        BaseRealmDAO<MockedRealmObject> baseRealmDAO = spy(new BaseRealmDAO<MockedRealmObject>
                (realmConfiguration, MockedRealmObject.class) {
            @Override
            public Realm getRealmInstance() {

                return realm;
            }

            @Override
            protected boolean hasPrimaryKey(Realm realm) {
                return true;
            }
        });

        // Prepare mocked models
        MockedRealmObject mockedRealmObject = spy(new MockedRealmObject());
        MockedRealmObject mockedRealmObject2 = spy(new MockedRealmObject());

        List<MockedRealmObject> mockedRealmObjectList = new ArrayList<>();
        mockedRealmObjectList.add(mockedRealmObject);
        mockedRealmObjectList.add(mockedRealmObject2);

        // Make test request
        baseRealmDAO.save(mockedRealmObjectList);

        // verify methods are called
        verify(realm, times(1)).beginTransaction();
        verify(realm, times(1)).commitTransaction();
        verify(mockedRealmObject, times(1)).setSavedDate(anyLong());
        verify(mockedRealmObject2, times(1)).setSavedDate(anyLong());
        verify(realm, times(1)).copyToRealmOrUpdate(eq(mockedRealmObjectList));
        verify(realm, times(1)).close();
    }

    @Test
    public void saveMultipleModelsWithoutPrimaryKey() throws Exception {

        // Need to extend base class, not allowed to have abstract instances
        BaseRealmDAO<MockedRealmObject> baseRealmDAO = spy(new BaseRealmDAO<MockedRealmObject>
                (realmConfiguration, MockedRealmObject.class) {
            @Override
            public Realm getRealmInstance() {

                return realm;
            }

            @Override
            protected boolean hasPrimaryKey(Realm realm) {
                return false;
            }
        });

        // Prepare mocked models
        MockedRealmObject mockedRealmObject = spy(new MockedRealmObject());
        MockedRealmObject mockedRealmObject2 = spy(new MockedRealmObject());

        List<MockedRealmObject> mockedRealmObjectList = new ArrayList<>();
        mockedRealmObjectList.add(mockedRealmObject);
        mockedRealmObjectList.add(mockedRealmObject2);

        // Make test request
        baseRealmDAO.save(mockedRealmObjectList);

        // verify methods are called
        verify(realm, times(1)).beginTransaction();
        verify(realm, times(1)).commitTransaction();
        verify(mockedRealmObject, times(1)).setSavedDate(anyLong());
        verify(mockedRealmObject2, times(1)).setSavedDate(anyLong());
        verify(realm, times(1)).copyToRealm(eq(mockedRealmObjectList));
        verify(realm, times(1)).close();
    }

}
