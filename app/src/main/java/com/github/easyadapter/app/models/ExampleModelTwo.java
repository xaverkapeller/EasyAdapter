package com.github.easyadapter.app.models;

import android.widget.CheckBox;

import com.github.easyadapter.EasyAdapter;
import com.github.easyadapter.annotations.BindToView;
import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.annotations.InjectView;
import com.github.easyadapter.annotations.Layout;
import com.github.easyadapter.annotations.OnCheckedChanged;
import com.github.easyadapter.api.ViewModel;
import com.github.easyadapter.app.R;
import com.github.easyadapter.impl.AbsViewHolder;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/02/15
 */
@Layout(R.layout.list_item_two)
public class ExampleModelTwo implements ViewModel {

    private final int textResId;

    private boolean checkedState = false;

    public ExampleModelTwo(int textResId) {
        this.textResId = textResId;
    }

    @BindToView(R.id.checkbox)
    public int getCheckBoxText() {
        return this.textResId;
    }

    @BindToView(R.id.checkbox)
    public boolean checkedState() {
        return checkedState;
    }

    @OnCheckedChanged(R.id.checkbox)
    public void onCheckedChanged(@InjectView(R.id.checkbox) CheckBox checkBox) {
        checkedState = checkBox.isChecked();
    }
}
