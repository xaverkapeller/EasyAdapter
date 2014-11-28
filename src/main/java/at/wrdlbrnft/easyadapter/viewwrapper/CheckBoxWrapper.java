package at.wrdlbrnft.easyadapter.viewwrapper;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.TextView;

import java.lang.reflect.Field;

import at.wrdlbrnft.easyadapter.helper.TypeHelper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 14/11/14
 */
class CheckBoxWrapper extends TextViewWrapper {

    public CheckBoxWrapper(Context context, CheckBox view) {
        super(context, view);
    }

    @Override
    protected boolean applyAutoDetect(TextView view, Field field, Object value) {
        return applyCheckedState(view, field, value) || super.applyAutoDetect(view, field, value);
    }

    @Override
    protected boolean applyCheckedState(TextView view, Field field, Object value) {
        final CheckBox checkBox = (CheckBox) view;

        if (value == null) {
            checkBox.setChecked(false);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isBoolean(type)) {
            checkBox.setChecked((Boolean) value);
            return true;
        }

        return false;
    }
}
