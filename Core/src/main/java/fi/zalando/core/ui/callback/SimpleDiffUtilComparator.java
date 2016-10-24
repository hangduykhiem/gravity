package fi.zalando.core.ui.callback;

/**
 * Simple implementation of {@link fi.zalando.core.ui.callback.DiffUtilCallback.DiffUtilComparator}
 *
 * Created by jduran on 21/10/16.
 */
public abstract class SimpleDiffUtilComparator<T>
        implements DiffUtilCallback.DiffUtilComparator<T> {

    public Object getChangePayload(T oldItem, T newItem) {
        return null;
    }
}
