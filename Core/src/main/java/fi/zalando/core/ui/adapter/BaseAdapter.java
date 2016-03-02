package fi.zalando.core.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import fi.zalando.core.ui.adapter.viewholder.BaseViewHolder;

/**
 * Abstract activity that holds common methods usable by all the {@link RecyclerView.Adapter} on the
 * app.
 *
 * Created by jduran on 12/01/16.
 */
public abstract class BaseAdapter<T, U extends BaseViewHolder<T>> extends RecyclerView.Adapter<U> {

    protected List<T> items;

    /**
     * Constructor
     *
     * @param items {@link List} of {@link T} to render in the list
     */
    protected BaseAdapter(@NonNull List<T> items) {

        this.items = items;
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        holder.bindData(items.get(position));
    }

    /**
     * Removes the item in the given position and updates the UI animating the removal.
     * @param index item's index
     */
    public void removeItem(int index) {
        items.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * Clears and updates the adapters item list with the given items, and calls
     * notifyDataSetChanged to refresh the UI.
     *
     * @param items List of items to be added to the adapter.
     */
    public void setItems(@NonNull final List<T> items) {

        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

}
