package com.github.easyadapter.app.models;

import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.annotations.Listener;
import com.github.easyadapter.annotations.OnCheckedChanged;
import com.github.easyadapter.annotations.OnClick;
import com.github.easyadapter.app.R;
import com.github.easyadapter.impl.AbsViewHolder;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/06/15
 */
@Listener(ExampleModel.class)
public interface ExampleListener {

    @OnClick(R.id.layout)
    public void onExampleModelClick(@Inject AbsViewHolder<ExampleModel> viewHolder);

    @OnCheckedChanged(R.id.checkBox)
    public void onExampleModelCheckedChange(@Inject AbsViewHolder<ExampleModel> viewHolder);
}
