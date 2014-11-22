package at.wrdlbrnft.easyadapter.viewwrapper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import at.wrdlbrnft.easyadapter.annotations.DateFormat;
import at.wrdlbrnft.easyadapter.annotations.DatePattern;
import at.wrdlbrnft.easyadapter.annotations.NumberFormat;
import at.wrdlbrnft.easyadapter.helper.DateHelper;
import at.wrdlbrnft.easyadapter.helper.TypeHelper;
import at.wrdlbrnft.easyadapter.helper.ViewHelper;

/**
 * Created by Xaver on 14/11/14.
 */
class TextViewWrapper extends BaseViewWrapper<TextView> {

    public TextViewWrapper(TextView view) {
        super(view);
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

        return applyText(view, field, value);
    }

    @Override
    protected boolean applyText(TextView view, Field field, Object value) {
        if(value == null) {
            view.setText("");
            return true;
        }

        final Class<?> type = field.getType();

        if(TypeHelper.isInteger(type)) {
            view.setText((Integer) value);
            return true;
        }

        if(TypeHelper.isNumber(type)) {
            final String text = getFormattedNumber(field, value);
            view.setText(text);
            return true;
        }

        if(type == Date.class) {
            final Date date = (Date) value;
            final String text = getFormattedDate(field, date);
            view.setText(text);
            return true;
        }

        if(type == Calendar.class) {
            final Calendar calendar = (Calendar) value;
            final Date date = calendar.getTime();
            final String text = getFormattedDate(field, date);
            view.setText(text);
            return true;
        }

        final String text = String.valueOf(value);
        view.setText(text);

        return true;
    }

    private String getFormattedNumber(Field field, Object number) {
        if(field.isAnnotationPresent(NumberFormat.class)) {
            final NumberFormat annotation = field.getAnnotation(NumberFormat.class);
            final String pattern = annotation.pattern();
            final DecimalFormat format = new DecimalFormat(pattern);
            return format.format(number);
        }

        return String.valueOf(number);
    }

    private String getFormattedDate(Field field, Date date) {
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
        } else if(field.isAnnotationPresent(DatePattern.class)) {
            final DatePattern annotation = field.getAnnotation(DatePattern.class);
            final String pattern = annotation.pattern();
            return DateHelper.format(date, pattern);
        } else {
            return DateHelper.formatDateTime(date);
        }
    }
}
