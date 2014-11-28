package at.wrdlbrnft.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.wrdlbrnft.easyadapter.enums.Property;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/10/14
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindToView {
    public int[] id();
    public Property property() default Property.AUTO_DETECT;
}
