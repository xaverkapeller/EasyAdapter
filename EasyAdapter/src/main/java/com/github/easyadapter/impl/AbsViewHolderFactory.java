package com.github.easyadapter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.easyadapter.EasyAdapter;
import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.api.ViewModel;

import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/02/15
 */
public abstract class AbsViewHolderFactory<T extends ViewModel> {

    protected AbsViewHolderFactory(LayoutInflater inflater, EasyAdapter.Injector injector) {

    }

    public abstract AbsViewHolder<T> newInstance(ViewGroup viewGroup);
}
