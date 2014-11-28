package at.wrdlbrnft.easyadapter.viewwrapper;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

import at.wrdlbrnft.easyadapter.enums.Property;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 14/11/14
 */
public interface ViewWrapper {

    public boolean bind(Property property, Field field, Object value);

    public View getView();

    public static class Factory {

        public static ViewWrapper wrap(Context context, View view) {
            if(view == null) {
                return new NullViewWrapper();
            }

            if(view instanceof CheckBox) {
                return new CheckBoxWrapper(context, (CheckBox) view);
            }

            if(view instanceof ImageView) {
                return new ImageViewWrapper(context, (ImageView) view);
            }

            if(view instanceof TextView) {
                return new TextViewWrapper(context, (TextView) view);
            }

           return new BaseViewWrapper<View>(context, view);
        }
    }
}
