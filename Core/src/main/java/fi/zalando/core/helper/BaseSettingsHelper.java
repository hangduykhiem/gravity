package fi.zalando.core.helper;

import fi.zalando.core.persistence.PersistentHashTable;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Base class for setting helper.
 *
 * Created by hduykhiem on 28/01/16.
 */
public abstract class BaseSettingsHelper {

    protected final PersistentHashTable persistentHashTable;

    /**
     * Constructor to create an implement of {@link PersistentHashTable} interface
     *
     * @param persistentHashTable implementation to be created
     */
    protected BaseSettingsHelper(PersistentHashTable persistentHashTable) {
        this.persistentHashTable = persistentHashTable;
    }

    /**
     * Creates a {@link BehaviorSubject} to use within settings using the given {@link Observable}
     * as a reference
     *
     * @param observable {@link Observable} to use as reference in the {@link Subject}
     * @param <T>        {@link T} of the {@link Observable}
     * @return {@link Subject} that listens and emits the given {@link Observable}
     */
    protected <T> Subject<T, T> createObservingSubject(Observable<T> observable) {

        Subject<T, T> behaviorSubject = BehaviorSubject.create();
        observable.subscribe(behaviorSubject::onNext, behaviorSubject::onError);
        return behaviorSubject;
    }

}
