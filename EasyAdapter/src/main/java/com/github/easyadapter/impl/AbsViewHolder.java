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

    private T mCurrentModel = null;

    public AbsViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(T model) {
        if (mCurrentModel != null) {
            performUnbind(mCurrentModel);
        }
        mCurrentModel = model;
        performBind(model);
    }

    protected abstract void performBind(T model);

    protected abstract void performUnbind(T model);
}
