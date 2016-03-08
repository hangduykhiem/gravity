package fi.zalando.core.persistence;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fi.zalando.core.data.model.Dateable;
import fi.zalando.core.data.model.annotation.RealmId;
import fi.zalando.core.helper.CleaningHelper;
import fi.zalando.core.persistence.event.RealmEvent;
import fi.zalando.core.utils.Preconditions;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmFieldType;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.Table;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * BaseDAO for storing and retrieving models into a Realm Database. Note! {@link Dateable} is
 * compulsory for {@link T} model since Realm does not support abstract objects.
 *
 * Created by jduran on 17/02/16.
 */
public abstract class BaseRealmDAO<T extends RealmObject & Dateable> implements CleaningHelper
        .Cleanable {

    private final RealmConfiguration realmConfiguration;
    private final Class<T> clazz;
    private final EventBus eventBus;
    private final RealmEvent<T> defaultRealmEvent;

    // Keep a cached observable at least for requests when all items are required
    private Observable<List<T>> cachedAllLoadedItems;

    /**
     * Constructor that provides the {@link Realm} database
     */
    protected BaseRealmDAO(RealmConfiguration realmConfiguration, Class<T> clazz, EventBus
            eventBus) {

        this.realmConfiguration = realmConfiguration;
        this.clazz = clazz;
        this.eventBus = eventBus;
        this.defaultRealmEvent = new RealmEvent<T>();

        checkCorrectModel();
    }

    /**
     * Clears completely the table for {@link T} class
     */
    @CallSuper
    public void clean() {

        clear(true);
        // Remove the cached observable
        cachedAllLoadedItems = null;
    }

    /**
     * Provides the amount of items stored in the DAO
     *
     * @return {@link Integer} with the amount of items stored in the DAO
     */
    public int count() {

        return getAll().size();
    }

    /**
     * Clears completely the table for {@link T} class. Allows the caller to decide whether or not
     * to notify the listeners.
     *
     * @param notifyListeners {@link Boolean} flag indicating if the listeners need to be notified
     */
    private void clear(boolean notifyListeners) {

        // Create the realm instance
        final Realm realm = getRealmInstance();
        // start transaction
        realm.beginTransaction();
        // Clear the table related with the class
        realm.clear(clazz);
        // Close transactions
        realm.commitTransaction();
        // Close the instance
        closeRealm(realm);
        // Send a general message if notify flag is on
        if (notifyListeners) {
            eventBus.post(defaultRealmEvent);
        }
    }

    /**
     * Deletes the model with the given {@link String} or {@link Long} id
     *
     * @param id {@link String} or {@link Long} to delete
     */
    public void delete(Object id) {

        Realm realm = getRealmInstance();
        // Make the query
        List<T> queryResults;
        // Cast id, we are sure it is long or string
        if (id instanceof String) {
            queryResults = realm.where(clazz).equalTo
                    (getPrimaryKeyFieldName(realm), (String) id).findAll();
        } else {
            queryResults = realm.where(clazz).equalTo
                    (getPrimaryKeyFieldName(realm), (Long) id).findAll();
        }
        // Save the model to send it later
        T modelToDelete = null;
        // Since it has a primary key, we are sure there is only one
        if (!queryResults.isEmpty()) {
            // Save the model to send it later
            modelToDelete = realm.copyFromRealm(queryResults.get(0));
            // Delete the item
            realm.beginTransaction();
            queryResults.get(0).removeFromRealm();
            realm.commitTransaction();
        }
        // Close the connection
        closeRealm(realm);

        // Send the event if something was removed
        if (modelToDelete != null) {
            eventBus.post(new RealmEvent<>(findRealmId(modelToDelete)));
        }
    }

    /**
     * Returns the {@link T} model that matches the given {@link String} or {@link Long} id. Null if
     * not found
     *
     * @param id {@link String} or {@link Long} with the id
     * @return {@link T} model matching the id, null if it does not exist
     */
    @Nullable
    private T getById(Object id) {

        Realm realm = getRealmInstance();
        // Make the query, since findall returns a list. Only return first one.
        // Since it has a primary key, we are sure there is only one
        List<T> queryResults;
        // Cast id, we are sure it is long or string
        if (id instanceof String) {
            queryResults = realm.copyFromRealm(realm.where(clazz).equalTo
                    (getPrimaryKeyFieldName(realm), (String) id).findAll());
        } else {
            queryResults = realm.copyFromRealm(realm.where(clazz).equalTo
                    (getPrimaryKeyFieldName(realm), (Long) id).findAll());
        }
        closeRealm(realm);

        if (!queryResults.isEmpty()) {
            return queryResults.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns all the items stored in the table.
     *
     * @return {@link List} with all the {@link T} items stored in the table
     */
    private List<T> getAll() {

        Realm realm = getRealmInstance();
        try {
            return realm.copyFromRealm(realm.where(clazz).findAll());
        } finally {
            closeRealm(realm);
        }
    }

    /**
     * Checks if the DAO table is empty
     *
     * @return {@link Boolean} indicating if the DAO table is empty
     */
    public boolean isEmpty() {

        return count() == 0;
    }

    /**
     * Provides an {@link Observable} that loads asynchronously a {@link List} with all the {@link
     * T} items in the database. It does never return a null list. If nothing exists, it will return
     * an empty List
     *
     * @return {@link Observable} to load all items in database
     */
    public Observable<List<T>> loadAll() {

        if (cachedAllLoadedItems == null) {

            // When this was implemented, realm 0.87 was not closing automatically if using their
            // built-in observables when unsubscribing observable
            cachedAllLoadedItems = Observable.create(new Observable.OnSubscribe<List<T>>() {

                private Subscriber<? super List<T>> subscriber;

                @Override
                public void call(Subscriber<? super List<T>> subscriber) {

                    this.subscriber = subscriber;

                    // Register an event bus so we will get a message
                    // when there is an update on the table
                    Object objectToRegister = this;
                    eventBus.register(objectToRegister);
                    subscriber.add(Subscriptions.create(() ->
                            eventBus.unregister(objectToRegister)));

                    subscriber.onNext(getAll());
                }

                @Subscribe
                @SuppressWarnings("unused")
                public void onUpdatedEvent(RealmEvent<T> realmEvent) {

                    Timber.d("Event received: " + realmEvent);
                    subscriber.onNext(getAll());
                }

            }).cache();
        }

        return cachedAllLoadedItems;
    }

    /**
     * Provides an {@link Observable} to load {@link T} model that matches given {@link String} id
     *
     * @param id {@link String} with the id of the model
     * @return {@link Observable} to load matching {@link T} model
     */
    public Observable<T> loadById(String id) {

        return loadByObjectId(id);
    }

    /**
     * Provides an {@link Observable} to load {@link T} model that matches given {@link Long} id
     *
     * @param id {@link Long} with the id of the model
     * @return {@link Observable} to load matching {@link T} model
     */
    public Observable<T> loadById(Long id) {

        return loadByObjectId(id);
    }

    /**
     * Provides an {@link Observable} to load {@link T} model that matches given {@link Object} id.
     * This DAO supports only String or Long Ids.
     *
     * @param id {@link String} or {@link Long} with the id of the model
     * @return {@link Observable} to load matching {@link T} model
     */
    private Observable<T> loadByObjectId(Object id) {

        // When this was implemented, realm 0.87 was not closing automatically if using their
        // built-in observables when unsubscribing observable
        return Observable.create(new Observable.OnSubscribe<T>() {

            private Subscriber<? super T> subscriber;

            @Override
            public void call(Subscriber<? super T> subscriber) {

                this.subscriber = subscriber;

                // Register an event bus so we will get a message
                // when there is an update on the table
                Object objectToRegister = this;
                eventBus.register(objectToRegister);
                subscriber.add(Subscriptions.create(() ->
                        eventBus.unregister(objectToRegister)));

                subscriber.onNext(getById(id));
            }

            @Subscribe
            @SuppressWarnings("unused")
            public void onUpdatedEvent(RealmEvent<T> realmEvent) {

                Timber.d("Event received: " + realmEvent);
                if (realmEvent.requiresUpdate(id)) {
                    subscriber.onNext(getById(id));
                }
            }
        });
    }

    /**
     * Replaces all the {@link T} items stored in the DAO with the given ones in the {@link
     * Iterable}
     *
     * @param modelsToReplace {@link T} models to store replacing the current ones
     */
    public void replace(Iterable<T> modelsToReplace) {

        clear(false);
        save(modelsToReplace);
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
        // Check if the model has primary key
        boolean hasPrimaryKey = hasPrimaryKeyAndRealmId(realm);
        // Close the instance
        closeRealm(realm);
        // Send the event
        eventBus.post(hasPrimaryKey ? new RealmEvent<>(findRealmId(modelToSave)) :
                defaultRealmEvent);
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
        if (hasPrimaryKey(realm)) {
            realm.copyToRealmOrUpdate(modelsToSave);
        } else {
            realm.copyToRealm(modelsToSave);
        }
        // Finalise the transaction
        realm.commitTransaction();
        // Check if the model has primary key
        boolean hasPrimaryKey = hasPrimaryKeyAndRealmId(realm);
        // Close the instance
        closeRealm(realm);

        // Send the events
        if (hasPrimaryKey) {
            // If contains primary send an event per saved id
            for (T modelToSave : modelsToSave) {
                eventBus.post(new RealmEvent<>(findRealmId(modelToSave)));
            }
        } else {
            // If no contains primary send a general event
            eventBus.post(defaultRealmEvent);
        }
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
     * Checks that the model to store is right
     */
    private void checkCorrectModel() {

        Realm realm = getRealmInstance();
        if (hasPrimaryKey(realm)) {
            // Check that the type contains a valid primary key, launch exception if not
            checkCorrectPrimaryKey(realm);
            // Check that the primary key type is a number
            Preconditions.checkArgument(getPrimaryKeyType(realm).equals(RealmFieldType.STRING) ||
                    getPrimaryKeyType(realm).equals(RealmFieldType.INTEGER), String.format("%s " +
                    "does not have a String or Long as primary key", clazz.getSimpleName()));
        }
        closeRealm(realm);
    }

    /**
     * Checks if {@link T} has a valid primary key. Throws IllegalStateException if not.
     */
    private void checkCorrectPrimaryKey(Realm realm) {

        // Check that the type contains a valid primary key
        Preconditions.checkState(hasValidPrimaryKeyType(realm), String.format("%s class " +
                "@PrimaryKey type is not supported", clazz.getSimpleName()));
        // Check that it contains a @RealmId annotation
        Preconditions.checkState(hasRealmId(), String.format("%s class does not " +
                "have a @RealmId", clazz.getSimpleName()));
    }

    /**
     * Closes the given {@link Realm} instance if it is still open
     *
     * @param instanceToClose {@link Realm} instance to close
     */
    private void closeRealm(Realm instanceToClose) {

        if (!instanceToClose.isClosed()) {
            instanceToClose.close();
        }
    }

    /**
     * Provides a {@link Realm} instance with the stored {@link RealmConfiguration}
     *
     * @return {@link Realm} instance
     */
    private Realm getRealmInstance() {

        return Realm.getInstance(realmConfiguration);
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
     * Checks if {@link T} has a primary key and realmId
     *
     * @param realm {@link Boolean} with the check result
     */
    private boolean hasPrimaryKeyAndRealmId(Realm realm) {

        return hasPrimaryKey(realm) && hasRealmId();
    }

    /**
     * Checks if {@link T} has a valid primary key
     *
     * @param realm {@link Boolean} with the check result
     */
    private boolean hasValidPrimaryKeyType(Realm realm) {

        // Parse class primary key RealmFieldType
        RealmFieldType realmFieldType = getPrimaryKeyType(realm);
        // This DAO will only support string based primary keys
        return realmFieldType.equals(RealmFieldType.STRING) || realmFieldType.equals
                (RealmFieldType.INTEGER);
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

    /**
     * Finds the realm id of the given model using java reflection
     *
     * @param modelToAnalyse {@link T} with the model to analyse
     * @return {@link Object} with the id annotated with {@link RealmId}
     */
    @Nullable
    private Object findRealmId(T modelToAnalyse) {

        try {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(RealmId.class)) {
                    return method.invoke(modelToAnalyse);
                }
            }
        } catch (IllegalAccessException e) {
            Timber.e(e, "findRealmId IllegalAccessException");
        } catch (InvocationTargetException e) {
            Timber.e(e, "findRealmId InvocationTargetException");
        }
        return null;
    }

    /**
     * Checks using java reflection if current {@link T} contains {@link RealmId} annotation
     *
     * @return {@link Boolean} with the check result
     */
    private boolean hasRealmId() {

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(RealmId.class)) {
                return true;
            }
        }
        return false;
    }

}
