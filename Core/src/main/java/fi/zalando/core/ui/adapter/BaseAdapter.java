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

}
