package fi.zalando.core.ui.callback;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Abstract DiffUtilCallback to use {@link DiffUtil} functionality on recyclerView adapters
 *
 * Created by jduran on 21/10/16.
 */
public class DiffUtilCallback<T> extends DiffUtil.Callback {

    /**
     * Interface to compare two items on the list
     *
     * @param <T> {@link T} type to compare
     */
    public interface DiffUtilComparator<T> {

        /**
         * Checks if the oldItem and newItem are the same (for example, they share same id). It does
         * not mean that they are equal! Something may have changed on {@link T}
         *
         * @param oldItem {@link T} oldItem to compare
         * @param newItem {@link T} newItem to compare
         * @return {@link Boolean} with the comparison result
         */
        boolean areItemsTheSame(T oldItem, T newItem);

        /**
         * When {@link #areItemsTheSame(T, T)} returns {@code true} for two items but their equal,
         * DiffUtil calls this method to get a payload about the change. <p> For example, if you are
         * using DiffUtil with {@link RecyclerView}, you can return the particular field that
         * changed in the item and your {@link android.support.v7.widget.RecyclerView.ItemAnimator
         * ItemAnimator} can use that information to run the correct animation. <p> Default
         * implementation returns {@code null}. <p>
         *
         * @param oldItem The position of the item in the old list
         * @param newItem The position of the item in the new list
         * @return A payload object that represents the change between the two items.
         * @see "http://bit.ly/2eAPQTp" to check an example
         */
        @Nullable
        Object getChangePayload(T oldItem, T newItem);

    }

    private final List<T> oldList;
    private final List<T> newList;
    private final DiffUtilComparator<T> comparator;

    public DiffUtilCallback(List<T> oldList, List<T> newList, DiffUtilComparator<T> comparator) {

        this.oldList = oldList;
        this.newList = newList;
        this.comparator = comparator;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return comparator.areItemsTheSame(oldList.get(oldItemPosition),
                newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

}
