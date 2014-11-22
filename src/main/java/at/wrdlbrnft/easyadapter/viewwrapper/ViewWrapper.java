package at.wrdlbrnft.easyadapter.viewwrapper;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by Xaver on 14/11/14.
 */
public interface ViewWrapper {

    public boolean bind(Property property, Field field, Object value);

    public View getView();

    public static class Factory {

        public static ViewWrapper wrap(View view) {
            if(view == null) {
                return new NullViewWrapper();
            }

            if(view instanceof CheckBox) {
                return new CheckBoxWrapper((CheckBox) view);
            }

            if(view instanceof ImageView) {
                return new ImageViewWrapper((ImageView) view);
            }

            if(view instanceof TextView) {
                return new TextViewWrapper((TextView) view);
            }

           return new BaseViewWrapper<View>(view);
        }
    }
}
