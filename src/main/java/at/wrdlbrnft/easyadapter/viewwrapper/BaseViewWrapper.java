package at.wrdlbrnft.easyadapter.viewwrapper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

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
    public boolean bind(Property property, Class<?> valueClass, Object value) {

        switch (property) {

            case AUTO_DETECT:
                return applyAutoDetect(mView, valueClass, value);

            case TEXT:
                return applyText(mView, valueClass, value);

            case IMAGE:
                return applyImage(mView, valueClass, value);

            case BACKGROUND:
                return applyBackground(mView, valueClass, value);

            case BACKGROUND_COLOR:
                return applyBackgroundColor(mView, valueClass, value);

            case ALPHA:
                return applyAlpha(mView, valueClass, value);

            case VISIBILITY:
                return applyVisibility(mView, valueClass, value);

            case TRANSLATION_X:
                return applyTranslationX(mView, valueClass, value);

            case TRANSLATION_Y:
                return applyTranslationY(mView, valueClass, value);

            case TRANSLATION_Z:
                return applyTranslationZ(mView, valueClass, value);

            case ROTATION:
                return applyRotation(mView, valueClass, value);

            case ROTATION_X:
                return applyRotationX(mView, valueClass, value);

            case ROTATION_Y:
                return applyRotationY(mView, valueClass, value);

            case CHECKED_STATE:
                return applyCheckedState(mView, valueClass, value);

            case ENABLED:
                return applyEnabled(mView, valueClass, value);

            default:
                return false;
        }
    }

    @Override
    public View getView() {
        return mView;
    }

    protected boolean applyAutoDetect(T view, Class<?> valueClass, Object value) {
        return false;
    }

    protected boolean applyText(T view, Class<?> valueClass, Object value) {
        return false;
    }

    protected boolean applyImage(T view, Class<?> valueClass, Object value) {
        return false;
    }

    protected boolean applyCheckedState(T view, Class<?> valueClass, Object value) {
        return false;
    }

    protected boolean applyBackground(T view, Class<?> valueClass, Object value) {

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
                ViewHelper.setBackground(view, new BitmapDrawable(mView.getResources(), (Bitmap) value));
            }
            return true;
        }

        if (TypeHelper.isInteger(valueClass)) {
            if (value == null) {
                ViewHelper.setBackground(view, null);
            } else {
                view.setBackgroundResource((Integer) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyBackgroundColor(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isInteger(valueClass)) {
            if (value == null) {
                view.setBackgroundColor(Color.TRANSPARENT);
            } else {
                view.setBackgroundColor((Integer) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyAlpha(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isNumber(valueClass)) {
            if (value == null) {
                view.setAlpha(0.0f);
            } else {
                view.setAlpha((Float) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyVisibility(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isInteger(valueClass)) {
            if (value == null) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility((Integer) value);
            }
            return true;
        }

        if (TypeHelper.isBoolean(valueClass)) {
            if (value == null) {
                view.setVisibility(View.GONE);
            } else {
                final boolean visible = (Boolean) value;
                view.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
            return true;
        }

        return false;
    }

    protected boolean applyTranslationX(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isNumber(valueClass)) {
            if (value == null) {
                view.setTranslationX(0.0f);
            } else {
                view.setTranslationX((Float) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyTranslationY(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isNumber(valueClass)) {
            if (value == null) {
                view.setTranslationY(0.0f);
            } else {
                view.setTranslationY((Float) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyTranslationZ(T view, Class<?> valueClass, Object value) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            if (TypeHelper.isNumber(valueClass)) {
                if (value == null) {
                    view.setTranslationZ(0.0f);
                } else {
                    view.setTranslationZ((Float) value);
                }
                return true;
            }
        }

        return false;
    }

    protected boolean applyRotation(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isNumber(valueClass)) {
            if (value == null) {
                view.setRotation(0.0f);
            } else {
                view.setRotation((Float) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyRotationX(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isNumber(valueClass)) {
            if (value == null) {
                view.setRotationX(0.0f);
            } else {
                view.setRotationX((Float) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyRotationY(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isNumber(valueClass)) {
            if (value == null) {
                view.setRotationY(0.0f);
            } else {
                view.setRotationY((Float) value);
            }
            return true;
        }

        return false;
    }

    protected boolean applyEnabled(T view, Class<?> valueClass, Object value) {

        if (TypeHelper.isBoolean(valueClass)) {
            if (value == null) {
                view.setEnabled(false);
            } else {
                view.setEnabled((Boolean) value);
            }
            return true;
        }

        return false;
    }
}
