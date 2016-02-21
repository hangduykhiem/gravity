package fi.zalando.core.persistence;

import java.util.List;
import java.util.concurrent.TimeUnit;

import fi.zalando.core.data.model.Dateable;
import fi.zalando.core.utils.Preconditions;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmFieldType;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.Table;
import rx.Observable;
import rx.Subscriber;

/**
 * BaseDAO for storing and retrieving models into a Realm Database. Note! {@link Dateable} is
 * compulsory for {@link T} model since Realm does not support abstract objects.
 *
 * Created by jduran on 17/02/16.
 */
public abstract class BaseRealmDAO<T extends RealmObject & Dateable> {

    private final RealmConfiguration realmConfiguration;
    private final Class<T> clazz;

    /**
     * Constructor that provides the {@link Realm} database
     */
    protected BaseRealmDAO(RealmConfiguration realmConfiguration, Class<T> clazz) {

        this.realmConfiguration = realmConfiguration;
        this.clazz = clazz;

        // Check that the generic type that is going to be used contains a valid primary key
        Realm realm = getRealmInstance();
        checkCorrectPrimaryKey(realm);
        closeRealm(realm);
    }

    /**
     * Provides a {@link Realm} instance with the stored {@link RealmConfiguration}
     *
     * @return {@link Realm} instance
     */
    protected Realm getRealmInstance() {

        return Realm.getInstance(realmConfiguration);
    }

    /**
     * Clears completely the table for {@link T} class
     */
    public void clear() {

        // Create the realm instance
        final Realm realm = getRealmInstance();
        // Clear the table related with the class
        realm.clear(clazz);
        // Close the instance
        closeRealm(realm);
    }

    /**
     * Deletes the given {@link T} model from the persistent storage. NOTE: Be sure that the
     * provided model has been loaded from database before calling this method.
     *
     * @param modelToDelete {@link T} model that will be deleted
     */
    public void delete(T modelToDelete) {

        // Create the realm instance
        final Realm realm = getRealmInstance();

        // Init the transaction and delete the model
        realm.beginTransaction();
        modelToDelete.removeFromRealm();
        realm.commitTransaction();

        // Close the instance
        closeRealm(realm);
    }

    /**
     * Provides an {@link Observable} that loads asynchronously a {@link List} with all the {@link
     * T} items in the database. It does never return a null list. If nothing exists, it will return
     * an empty List
     *
     * @return {@link Observable} to load all items in database
     */
    public Observable<List<T>> loadAll() {

        // When this was implemented, realm 0.87 was not closing automatically if using their
        // built-in observables when unsubscribing observable

        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(Subscriber<? super List<T>> subscriber) {
                Realm realm = getRealmInstance();
                subscriber.onNext(realm.copyFromRealm(realm.where(clazz).findAll()));
                closeRealm(realm);
                // finish subscription
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Provides an {@link Observable} to load {@link T} model that matches given {@link String} id
     *
     * @param id {@link String} with the id of the model
     * @return {@link Observable} to load matching {@link T} model
     */
    public Observable<T> loadById(String id) {

        // When this was implemented, realm 0.87 was not closing automatically if using their
        // built-in observables when unsubscribing observable
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                Realm realm = getRealmInstance();
                // Check that the type contains a valid primary key, launch exception if not
                checkCorrectPrimaryKey(realm);
                // Check that the primary key type is a number
                Preconditions.checkArgument(getPrimaryKeyType(realm).equals(RealmFieldType.STRING),
                        String.format("%s does not have a String as primary key", clazz
                                .getSimpleName()));

                // Make the query, since findall returns a list. Only return first one.
                // Since it has a primary key, we are sure there is only one
                List<T> queryResults = realm.copyFromRealm(realm.where(clazz).equalTo
                        (getPrimaryKeyFieldName(realm), id).findAll());
                if (!queryResults.isEmpty()) {
                    subscriber.onNext(queryResults.get(0));
                } else {
                    subscriber.onNext(null);
                }
                closeRealm(realm);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Provides an {@link Observable} to load {@link T} model that matches given {@link Long} id
     *
     * @param id {@link Long} with the id of the model
     * @return {@link Observable} to load matching {@link T} model
     */
    public Observable<T> loadById(long id) {

        // When this was implemented, realm 0.87 was not closing automatically if using their
        // built-in observables when unsubscribing observable
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                Realm realm = getRealmInstance();
                // Check that the type contains a valid primary key, launch exception if not
                checkCorrectPrimaryKey(realm);
                // Check that the primary key type is a number
                Preconditions.checkArgument(getPrimaryKeyType(realm).equals(RealmFieldType.INTEGER),
                        String.format("%s does not have a numeric primary key", clazz
                                .getSimpleName()));

                // Make the query, since findall returns a list. Only return first one.
                // Since it has a primary key, we are sure there is only one
                List<T> queryResults = realm.copyFromRealm(realm.where(clazz).equalTo
                        (getPrimaryKeyFieldName(realm), id).findAll());
                if (!queryResults.isEmpty()) {
                    subscriber.onNext(queryResults.get(0));
                } else {
                    subscriber.onNext(null);
                }
                closeRealm(realm);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Saves (create or update) the given {@link T} model in the persistent storage. If the purpose
     * is to update the model, be sure that the {@link PrimaryKey} annotation is present in the
     * given model. {@link PrimaryKey} annotation is used to identify the model.
     *
     * @param modelToSave {@link T} model to save
     */
    public void save(T modelToSave) {

        // Create the realm instance
        final Realm realm = getRealmInstance();
        // Init the transaction and copy the model to database
        realm.beginTransaction();
        // Set current date as saved date
        modelToSave.setSavedDate(System.currentTimeMillis());
        // Check if we need to save it or update
        // It depends if it has primary key or not
        if (hasPrimaryKey(realm)) {
            realm.copyToRealmOrUpdate(modelToSave);
        } else {
            realm.copyToRealm(modelToSave);
        }
        // Finalise the transaction
        realm.commitTransaction();
        // Close the instance
        closeRealm(realm);
    }

    /**
     * Saves (create or update) the given {@link Iterable} of {@link T} model in the persistent
     * storage. If the purpose is to update the models, be sure that the @PrimaryKey annotation is
     * present in the given model.
     *
     * @param modelsToSave {@link List} of {@link T} models to save
     */
    public void save(Iterable<T> modelsToSave) {

        // Create the realm instance
        final Realm realm = getRealmInstance();
        // Init the transaction and copy the model to database
        realm.beginTransaction();
        // Set current date as saved date to all items to save
        long currentTimeMillis = System.currentTimeMillis();
        for (T modelToSave : modelsToSave) {
            modelToSave.setSavedDate(currentTimeMillis);
        }
        // Check if we need to save it or update
        // It depends if it has primary key or not
        if (realm.getTable(clazz).hasPrimaryKey()) {
            realm.copyToRealmOrUpdate(modelsToSave);
        } else {
            realm.copyToRealm(modelsToSave);
        }
        // Finalise the transaction
        realm.commitTransaction();
        // Close the instance
        closeRealm(realm);
    }

    /**
     * Utility method to check if the object is expired according to given parameters
     *
     * @param modelToCheck {@link T} model to check if it is expired
     * @param liveTime     {@link Long} with the live time
     * @param timeUnit     {@link TimeUnit} of the given live time
     * @return {@link Boolean} indicating if the object is expired
     */
    public final boolean hasExpired(T modelToCheck, long liveTime, TimeUnit timeUnit) {

        // If saved date is 0L, means that it has never been saved. Fresh data, so non expired
        return modelToCheck.getSavedDate() != 0L && modelToCheck.getSavedDate() + timeUnit
                .toMillis(liveTime) <= System.currentTimeMillis();
    }

    /**
     * Closes the given {@link Realm} instance if it is still open
     *
     * @param instanceToClose {@link Realm} instance to close
     */
    protected void closeRealm(Realm instanceToClose) {

        if (!instanceToClose.isClosed()) {
            instanceToClose.close();
        }
    }

    /**
     * Checks if {@link T} has a valid primary key. Throws IllegalStateException if not.
     */
    private void checkCorrectPrimaryKey(Realm realm) {

        // If primary key is provided check it is valid
        if (hasPrimaryKey(realm)) {
            // Check that the type contains a valid primary key
            Preconditions.checkState(hasPrimaryKey(realm), String.format("%s class does not " +
                    "have a @PrimaryKey", clazz.getSimpleName()));
            Preconditions.checkState(hasValidPrimaryKeyType(realm), String.format("%s class " +
                    "@PrimaryKey type is not supported", clazz.getSimpleName()));
        }
    }

    /**
     * Checks if {@link T} has a primary key
     *
     * @param realm {@link Boolean} with the check result
     */
    private boolean hasPrimaryKey(Realm realm) {

        return realm.getTable(clazz).hasPrimaryKey();
    }

    /**
     * Checks if {@link T} has a valid primary key
     *
     * @param realm {@link Boolean} with the check result
     */
    private boolean hasValidPrimaryKeyType(Realm realm) {

        // Parse class primary key RealmFieldType
        RealmFieldType realmFieldType = getPrimaryKeyType(realm);
        // This DAO will only support number or string primary keys
        return realmFieldType.equals(RealmFieldType.INTEGER) || realmFieldType.equals
                (RealmFieldType.STRING);
    }

    /**
     * Provides the name of the {@link PrimaryKey} field
     *
     * @param realm {@link Realm} opened instance
     * @return {@link String} with the primary key field name
     */
    private String getPrimaryKeyFieldName(Realm realm) {

        // Get the table
        Table typeTable = realm.getTable(clazz);
        // Return the column name
        return typeTable.getColumnName(typeTable.getPrimaryKey());
    }

    /**
     * Provides the {@link RealmFieldType} of the {@link PrimaryKey}
     *
     * @param realm {@link Realm} opened instance
     * @return {@link RealmFieldType} of the {@link PrimaryKey}
     */
    private RealmFieldType getPrimaryKeyType(Realm realm) {

        // Get the table
        Table typeTable = realm.getTable(clazz);
        // Parse class primary key RealmFieldType
        return typeTable.getColumnType(typeTable.getPrimaryKey());
    }

}
