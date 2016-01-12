package fi.zalando.core.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Base class to hold common {@link RecyclerView.ViewHolder}
 *
 * Created by jduran on 12/01/16.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    /**
     * Constructor
     *
     * @param itemView {@link View} that the view holder hosts
     */
    public BaseViewHolder(View itemView) {

        super(itemView);
        // Inflate items in ViewHolder with butterknife
        ButterKnife.bind(this, itemView);
    }

    /**
     * Binds the {@link T} model with the {@link RecyclerView.ViewHolder}
     *
     * @param modelToBind {@link T} model that will be bound with the {@link
     *                    RecyclerView.ViewHolder}
     */
    public abstract void bindData(@NonNull T modelToBind);
}
