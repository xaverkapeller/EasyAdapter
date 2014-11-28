package at.wrdlbrnft.easyadapter.viewwrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.reflect.Field;

import at.wrdlbrnft.easyadapter.helper.TypeHelper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 14/11/14
 */
class ImageViewWrapper extends BaseViewWrapper<ImageView> {

    public ImageViewWrapper(Context context, ImageView view) {
        super(context, view);
    }

    @Override
    protected boolean applyAutoDetect(ImageView view, Field field, Object value) {
        return applyImage(view, field, value) || applyImageResource(view, field, value);
    }

    @Override
    protected boolean applyImage(ImageView view, Field field, Object value) {
        if (value == null) {
            view.setImageDrawable(null);
            return true;
        }

        final Class<?> type = field.getType();

        if (type == Drawable.class) {
            view.setImageDrawable((Drawable) value);
            return true;
        }

        if (type == Bitmap.class) {
            view.setImageBitmap((Bitmap) value);
            return true;
        }

        return false;
    }

    @Override
    protected boolean applyImageResource(ImageView view, Field field, Object value) {
        if (value == null) {
            view.setImageDrawable(null);
            return true;
        }

        final Class<?> type = field.getType();

        if (TypeHelper.isInteger(type)) {
            view.setImageResource((Integer) value);
            return true;
        }

        return false;
    }
}
