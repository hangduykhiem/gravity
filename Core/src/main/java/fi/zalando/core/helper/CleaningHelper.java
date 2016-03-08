package fi.zalando.core.helper;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.zalando.core.utils.Preconditions;
import timber.log.Timber;

/**
 * Class responsible of cleaning objects
 *
 * Created by jduran on 01/03/16.
 */
public class CleaningHelper {

    private final List<Cleanable> cleanables;
    private static EventBus sEventBus;

    /**
     * Interface that defines methods that classes that require some cleaning tasks need to
     * implement
     */
    public interface Cleanable {

        /**
         * Executes the cleaning tasks
         */
        public void clean();

    }

    /**
     * Constructor
     *
     * @param eventBus {@link EventBus} instance
     */
    public CleaningHelper(EventBus eventBus) {

        cleanables = new ArrayList<>();

        initEventBus(eventBus);
    }

    /**
     * Constructor
     *
     * @param cleanableObjects {@link Cleanable} array that adds the cleanable directly to the
     *                         helper
     */
    public CleaningHelper(EventBus eventBus, @NonNull Cleanable... cleanableObjects) {

        cleanables = new ArrayList<>();
        addCleanables(cleanableObjects);

        initEventBus(eventBus);
    }

    /**
     * Initialises event bus instance to listen for global cleaning events
     *
     * @param eventBus {@link EventBus} instance to initialise
     */
    private void initEventBus(EventBus eventBus) {

        // First, unregister previous instance if exists
        if (sEventBus != null && sEventBus.isRegistered(this)) {
            sEventBus.unregister(this);
        }

        sEventBus = eventBus;
        if (!sEventBus.isRegistered(this)) {
            sEventBus.register(this);
        }
    }

    /**
     * Add a bunch of {@link Cleanable} items to the cleaning list
     *
     * @param cleanableObjects {@link Cleanable} array to add to the helper
     */
    public void addCleanables(@NonNull Cleanable... cleanableObjects) {

        Preconditions.checkNotNull(cleanableObjects);
        cleanables.addAll(Arrays.asList(cleanableObjects));
    }

    /**
     * Cleans all the objects given in the constructor
     */
    public void clean() {

        for (Cleanable cleanable : cleanables) {
            cleanable.clean();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onForceCleanEvent(CleaningEvent cleaningEvent) {

        Timber.d("Force clean event received");
        clean();
    }

    /**
     * Forces to clean all registered {@link Cleanable}s of the app
     */
    public static void forceClean() {

        sEventBus.post(new CleaningEvent());
    }

    /**
     * Cleaning event class
     */
    private static class CleaningEvent {

    }

}
