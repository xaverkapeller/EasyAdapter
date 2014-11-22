package at.wrdlbrnft.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 22/11/14
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumberFormat {
    public String pattern();
}
