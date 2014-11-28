package at.wrdlbrnft.easyadapter.helper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/11/14
 */
public class TypeHelper {

    public static boolean isOneOf(Class<?> type, Class<?>... classes) {
        for(Class<?> cls : classes) {
            if(type == cls) {
                return true;
            }
        }

        return false;
    }

    public static boolean isBoolean(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    public static boolean isInteger(Class<?> type) {
        return type == int.class || type == Integer.class;
    }

    public static boolean isFloat(Class<?> type) {
        return type == float.class || type == Float.class;
    }

    public static boolean isDouble(Class<?> type) {
        return type == double.class || type == Double.class;
    }

    public static boolean isNumber(Class<?> type) {
        return isInteger(type) || isFloat(type) || isDouble(type);
    }
}

