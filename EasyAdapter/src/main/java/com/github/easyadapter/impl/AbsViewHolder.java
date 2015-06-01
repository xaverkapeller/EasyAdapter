package com.github.easyadapter.impl;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.easyadapter.api.ViewModel;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 26/01/15
 */
public abstract class AbsViewHolder<T extends ViewModel> extends RecyclerView.ViewHolder {

    public T itemModel = null;

    public AbsViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(T model) {
        if (itemModel != null) {
            performUnbind(itemModel);
        }
        itemModel = model;
        performBind(model);
    }

    public void onAttach() {

    }

    public void onDetach() {
        if(itemModel != null) {
            performUnbind(itemModel);
        }
    }

    protected abstract void performBind(T model);

    protected abstract void performUnbind(T model);
}
