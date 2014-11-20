package at.wrdlbrnft.easyadapter.viewwrapper;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import at.wrdlbrnft.easyadapter.helper.TypeHelper;
import at.wrdlbrnft.easyadapter.helper.ViewHelper;

/**
 * Created by Xaver on 14/11/14.
 */
class ImageViewWrapper extends BaseViewWrapper<ImageView> {

    public ImageViewWrapper(ImageView view) {
        super(view);
    }

    @Override
    protected boolean applyAutoDetect(ImageView view, Class<?> valueClass, Object value) {
        return applyImage(view, valueClass, value);
    }

    @Override
    protected boolean applyImage(ImageView view, Class<?> valueClass, Object value) {

        if (valueClass == Drawable.class) {
            if (value == null) {
                view.setImageDrawable(null);
            } else {
                view.setImageDrawable((Drawable) value);
            }
            return true;
        }

        if (valueClass == Bitmap.class) {
            if (value == null) {
                view.setImageBitmap(null);
            } else {
                view.setImageBitmap((Bitmap) value);
            }
            return true;
        }

        if (TypeHelper.isInteger(valueClass)) {
            if (value == null) {
                view.setImageDrawable(null);
            } else {
                view.setImageResource((Integer) value);
            }
            return true;
        }

        return false;
    }
}
