package com.github.easyadapter.app.models;

import android.widget.CheckBox;

import com.github.easyadapter.annotations.BindToView;
import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.annotations.InjectView;
import com.github.easyadapter.annotations.Layout;
import com.github.easyadapter.annotations.Listener;
import com.github.easyadapter.annotations.OnCheckedChanged;
import com.github.easyadapter.annotations.OnClick;
import com.github.easyadapter.api.ViewModel;
import com.github.easyadapter.app.R;
import com.github.easyadapter.impl.AbsViewHolder;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 29/01/15
 */
@Layout(R.layout.list_item_one)
public class ExampleModel implements ViewModel {

    private final String mText;

    private boolean mChecked = false;

    public ExampleModel(String text) {
        this.mText = text;
    }

    @BindToView(R.id.textView)
    public String getText() {
        return mText;
    }

    @BindToView(R.id.checkBox)
    public boolean isChecked() {
        return mChecked;
    }

    @OnCheckedChanged(R.id.checkBox)
    public void onCheckedChange(@InjectView(R.id.checkBox) CheckBox checkBox) {
        mChecked = checkBox.isChecked();
    }
}
