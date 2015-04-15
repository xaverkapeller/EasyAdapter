package com.github.easyadapter.app.models;

import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.annotations.Listener;
import com.github.easyadapter.annotations.OnClick;
import com.github.easyadapter.app.R;
import com.github.easyadapter.impl.AbsViewHolder;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/02/15
 */
@Listener(ExampleModelOne.class)
public interface ExampleListener {

    @OnClick(R.id.layout)
    public void onClick(@Inject AbsViewHolder<ExampleModelOne> viewHolder);
}
