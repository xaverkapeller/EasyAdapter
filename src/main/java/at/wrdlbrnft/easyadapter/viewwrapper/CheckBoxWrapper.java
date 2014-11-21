package at.wrdlbrnft.easyadapter.viewwrapper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import android.widget.TextView;

import at.wrdlbrnft.easyadapter.helper.TypeHelper;
import at.wrdlbrnft.easyadapter.helper.ViewHelper;

/**
 * Created by Xaver on 14/11/14.
 */
class CheckBoxWrapper extends BaseViewWrapper<CheckBox> {

    public CheckBoxWrapper(CheckBox view) {
        super(view);
    }

    @Override
    protected boolean applyAutoDetect(CheckBox view, Class<?> valueClass, Object value) {

        if (valueClass == Drawable.class) {
            if (value == null) {
                ViewHelper.setBackground(view, null);
            } else {
                ViewHelper.setBackground(view, (Drawable) value);
            }
            return true;
        }

        if (valueClass == Bitmap.class) {
            if (value == null) {
                ViewHelper.setBackground(view, null);
            } else {
                ViewHelper.setBackground(view, new BitmapDrawable(view.getResources(), (Bitmap) value));
            }
            return true;
        }

        return applyCheckedState(view, valueClass, value) || applyText(view, valueClass, value);
    }

    @Override
    protected boolean applyCheckedState(CheckBox view, Class<?> valueClass, Object value) {

        if (TypeHelper.isBoolean(valueClass)) {
            if(value == null) {
                view.setChecked(false);
            } else {
                view.setChecked((Boolean) value);
            }
            return true;
        }

        return false;
    }

    @Override
    protected boolean applyText(CheckBox view, Class<?> valueClass, Object value) {

        if(TypeHelper.isInteger(valueClass)) {
            if(value == null) {
                view.setText("");
            } else {
                view.setText((Integer) value);
            }
            return true;
        }

        if(value == null) {
            view.setText("");
        } else {
            final String text = String.valueOf(value);
            view.setText(text);
        }

        return true;
    }
}
