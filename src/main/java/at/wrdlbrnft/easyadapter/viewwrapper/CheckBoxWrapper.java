package at.wrdlbrnft.easyadapter.viewwrapper;

import android.widget.CheckBox;
import android.widget.TextView;

import java.lang.reflect.Field;

import at.wrdlbrnft.easyadapter.helper.TypeHelper;

/**
 * Created by Xaver on 14/11/14.
 */
class CheckBoxWrapper extends TextViewWrapper {

    public CheckBoxWrapper(CheckBox view) {
        super(view);
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
