package at.wrdlbrnft.easyadapter.viewwrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Calendar;

import at.wrdlbrnft.easyadapter.annotations.DateFormat;
import at.wrdlbrnft.easyadapter.annotations.Format;
import at.wrdlbrnft.easyadapter.helper.DateHelper;
import at.wrdlbrnft.easyadapter.helper.TypeHelper;
import at.wrdlbrnft.easyadapter.helper.ViewHelper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 14/11/14
 */
class TextViewWrapper extends BaseViewWrapper<TextView> {

    public TextViewWrapper(Context context, TextView view) {
        super(context, view);
    }

    @Override
    protected boolean applyAutoDetect(TextView view, Field field, Object value) {
        if (value == null) {
            ViewHelper.setBackground(view, null);
            return true;
        }

        final Class<?> type = field.getType();

        if (type == Drawable.class) {
            ViewHelper.setBackground(view, (Drawable) value);
            return true;
        }

        if (type == Bitmap.class) {
            ViewHelper.setBackground(view, new BitmapDrawable(view.getResources(), (Bitmap) value));
            return true;
        }

        return applyTextResource(view, field, value) || applyText(view, field, value);
    }

    @Override
    protected boolean applyText(TextView view, Field field, Object value) {
        if(value == null) {
            view.setText("");
            return true;
        }

        final Class<?> type = field.getType();

        if(type == java.util.Date.class) {
            final java.util.Date date = (java.util.Date) value;
            final String text = getFormattedDate(field, date);
            view.setText(text);
            return true;
        }

        if(type == Calendar.class) {
            final Calendar calendar = (Calendar) value;
            final java.util.Date date = calendar.getTime();
            final String text = getFormattedDate(field, date);
            view.setText(text);
            return true;
        }

        final String text = getFormattedString(field, value);
        view.setText(text);

        return true;
    }

    @Override
    protected boolean applyTextResource(TextView view, Field field, Object value) {
        if(value == null) {
            view.setText("");
            return true;
        }

        final Class<?> type = field.getType();

        if(TypeHelper.isInteger(type)) {
            view.setText((Integer) value);
            return true;
        }

        return false;
    }

    private String getFormattedString(Field field, Object object) {
        if(field.isAnnotationPresent(Format.class)) {
            final Format annotation = field.getAnnotation(Format.class);
            final int patternId = annotation.pattern();
            final String pattern = getResourceString(patternId);
            return String.format(pattern, object);
        }

        return String.valueOf(object);
    }

    private String getFormattedDate(Field field, java.util.Date date) {
        if(field.isAnnotationPresent(DateFormat.class)) {
            final DateFormat annotation = field.getAnnotation(DateFormat.class);
            switch (annotation.format()) {
                case TIME: return DateHelper.formatTime(date);
                case DATE: return DateHelper.formatDate(date);
                case DATE_TIME: return DateHelper.formatDateTime(date);
                case SHORT_TIME: return DateHelper.formatShortTime(date);
                case SHORT_DATE: return DateHelper.formatShortDate(date);
                case SHORT_DATE_TIME: return DateHelper.formatShortDateTime(date);
                default: return "";
            }
        } else if(field.isAnnotationPresent(Format.class)) {
            final Format annotation = field.getAnnotation(Format.class);
            final int patternId = annotation.pattern();
            final String pattern = getResourceString(patternId);
            return DateHelper.format(date, pattern);
        } else {
            return DateHelper.formatDateTime(date);
        }
    }
}
