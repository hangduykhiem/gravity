package org.zalando.core.ui.callback;

/**
 * Simple implementation of {@link org.zalando.core.ui.callback.DiffUtilCallback.DiffUtilComparator}
 */
public abstract class SimpleDiffUtilComparator<T>
    implements DiffUtilCallback.DiffUtilComparator<T> {

  @Override
  public Object getChangePayload(T oldItem, T newItem) {
    return null;
  }
}
