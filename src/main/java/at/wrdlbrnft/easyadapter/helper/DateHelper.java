package at.wrdlbrnft.easyadapter.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 22/11/14
 */
public class DateHelper {

    private static final DateFormat mDateFormat = DateFormat.getDateInstance();
    private static final DateFormat mDateTimeFormat = DateFormat.getDateTimeInstance();
    private static final DateFormat mTimeFormat = DateFormat.getTimeInstance();
    private static final DateFormat mShortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static final DateFormat mShortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    private static final DateFormat mShortDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public static String format(Date date, String pattern) {
        if (date != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(date);
        }
        return null;
    }

    public static String formatDate(Date date) {
        if (date != null) {
            return mDateFormat.format(date);
        }
        return null;
    }

    public static String formatDateTime(Date date) {
        if (date != null) {
            return mDateTimeFormat.format(date);
        }
        return null;
    }

    public static String formatTime(Date date) {
        if (date != null) {
            return mTimeFormat.format(date);
        }
        return null;
    }

    public static String formatShortTime(Date date) {
        if (date != null) {
            return mShortTimeFormat.format(date);
        }
        return null;
    }

    public static String formatShortDate(Date date) {
        if (date != null) {
            return mShortDateFormat.format(date);
        }
        return null;
    }

    public static String formatShortDateTime(Date date) {
        if (date != null) {
            return mShortDateTimeFormat.format(date);
        }
        return null;
    }
}
