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
    private static EventBus eventBus = EventBus.getDefault();

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
     */
    public CleaningHelper() {

        cleanables = new ArrayList<>();

        initEventBus();
    }

    /**
     * Constructor
     *
     * @param cleanableObjects {@link Cleanable} array that adds the cleanable directly to the
     *                         helper
     */
    public CleaningHelper(@NonNull Cleanable... cleanableObjects) {

        cleanables = new ArrayList<>();
        addCleanables(cleanableObjects);

        initEventBus();
    }

    /**
     * Initialises event bus instance to listen for global cleaning events
     */
    private void initEventBus() {

        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
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

        eventBus.post(new CleaningEvent());
    }

    /**
     * Cleaning event class
     */
    private static class CleaningEvent {

    }

}
