package com.github.easyadapter.app.models;

import com.github.easyadapter.annotations.BindToView;
import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.annotations.Layout;
import com.github.easyadapter.annotations.OnClick;
import com.github.easyadapter.api.Property;
import com.github.easyadapter.api.ViewModel;
import com.github.easyadapter.app.R;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 29/01/15
 */
@Layout(R.layout.list_item_one)
public class ExampleModelOne implements ViewModel {

    private final String text;
    private final int drawableRes;

    public ExampleModelOne(String text, int drawableRes) {
        this.text = text;
        this.drawableRes = drawableRes;
    }

    @BindToView(R.id.textView)
    public String getText() {
        return text;
    }

    @BindToView(value = R.id.layout, property = Property.BACKGROUND)
    public int getBackground() {
        return drawableRes;
    }
}
