package at.wrdlbrnft.easyadapter.viewwrapper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.reflect.Field;

import at.wrdlbrnft.easyadapter.helper.TypeHelper;
import at.wrdlbrnft.easyadapter.helper.ViewHelper;

/**
 * Created by Xaver on 14/11/14.
 */
class BaseViewWrapper<T extends View> implements ViewWrapper {

    private final T mView;

    public BaseViewWrapper(T view) {
        mView = view;
    }

    @Override
    public boolean bind(Property property, Field field, Object value) {

        switch (property) {

            case AUTO_DETECT:
                return applyAutoDetect(mView, field, value);

            case TEXT:
                return applyText(mView, field, value);

            case IMAGE:
                return applyImage(mView, field, value);

            case BACKGROUND:
                return applyBackground(mView, field, value);

            case BACKGROUND_COLOR:
                return applyBackgroundColor(mView, field, value);

            case ALPHA:
                return applyAlpha(mView, field, value);

            case VISIBILITY:
                return applyVisibility(mView, field, value);

            case TRANSLATION_X:
                return applyTranslationX(mView, field, value);

            case TRANSLATION_Y:
                return applyTranslationY(mView, field, value);

            case TRANSLATION_Z:
                return applyTranslationZ(mView, field, value);

            case ROTATION:
                return applyRotation(mView, field, value);

            case ROTATION_X:
                return applyRotationX(mView, field, value);

            case ROTATION_Y:
                return applyRotationY(mView, field, value);

            case CHECKED_STATE:
                return applyCheckedState(mView, field, value);

            case ENABLED:
                return applyEnabled(mView, field, value);

            default:
                return false;
        }
    }

    @Override
    public View getView() {
        return mView;
    }

    protected boolean applyAutoDetect(T view, Field field, Object value) {
        return false;
    }

    protected boolean applyText(T view, Field field, Object value) {
        return false;
    }

    protected boolean applyImage(T view, Field field, Object value) {
        return false;
    }

    protected boolean applyCheckedState(T view, Field field, Object value) {
        return false;
    }

    protected boolean applyBackground(T view, Field field, Object value) {

        if(value == null) {
            ViewHelper.setBackground(view, null);
            return true;
        }

        final Class<?> type = field.getType();

        if (type == Drawable.class) {
            ViewHelper.setBackground(view, (Drawable) value);
            return true;
        }

        if (type == Bitmap.class) {
            ViewHelper.setBackground(view, new BitmapDrawable(mView.getResources(), (Bitmap) value));
            return true;
        }

        if (TypeHelper.isInteger(type)) {
            view.setBackgroundResource((Integer) value);
            return true;
        }

        return false;
    }

    protected boolean applyBackgroundColor(T view, Field field, Object value) {
        if(value == null) {
            view.setBackgroundColor(Color.TRANSPARENT);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isInteger(type)) {
            view.setBackgroundColor((Integer) value);
            return true;
        }

        return false;
    }

    protected boolean applyAlpha(T view, Field field, Object value) {
        if(value == null) {
            view.setAlpha(0.0f);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isNumber(type)) {
            view.setAlpha((Float) value);
            return true;
        }

        return false;
    }

    protected boolean applyVisibility(T view, Field field, Object value) {
        if (value == null) {
            view.setVisibility(View.GONE);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isInteger(type)) {
            view.setVisibility((Integer) value);
            return true;
        }

        if (TypeHelper.isBoolean(type)) {
            final boolean visible = (Boolean) value;
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            return true;
        }

        return false;
    }

    protected boolean applyTranslationX(T view, Field field, Object value) {
        if (value == null) {
            view.setTranslationX(0.0f);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isNumber(type)) {
            view.setTranslationX((Float) value);
            return true;
        }

        return false;
    }

    protected boolean applyTranslationY(T view, Field field, Object value) {
        if (value == null) {
            view.setTranslationY(0.0f);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isNumber(type)) {
            view.setTranslationY((Float) value);
            return true;
        }

        return false;
    }

    protected boolean applyTranslationZ(T view, Field field, Object value) {
        if (value == null) {
            view.setTranslationZ(0.0f);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isNumber(type)) {
            view.setTranslationZ((Float) value);
            return true;
        }

        return false;
    }

    protected boolean applyRotation(T view, Field field, Object value) {
        if (value == null) {
            view.setRotation(0.0f);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isNumber(type)) {
            view.setRotation((Float) value);
            return true;
        }

        return false;
    }

    protected boolean applyRotationX(T view, Field field, Object value) {
        if (value == null) {
            view.setRotationX(0.0f);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isNumber(type)) {
            view.setRotationX((Float) value);
            return true;
        }

        return false;
    }

    protected boolean applyRotationY(T view, Field field, Object value) {
        if (value == null) {
            view.setRotationY(0.0f);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isNumber(type)) {
            view.setRotationY((Float) value);
            return true;
        }

        return false;
    }

    protected boolean applyEnabled(T view, Field field, Object value) {
        if (value == null) {
            view.setEnabled(false);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isBoolean(type)) {
            view.setEnabled((Boolean) value);
            return true;
        }

        return false;
    }
}
