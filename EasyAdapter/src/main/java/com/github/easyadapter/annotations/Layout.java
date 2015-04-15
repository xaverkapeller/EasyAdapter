package com.github.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 26/01/15
 */
@Target(ElementType.TYPE)
public @interface Layout {
    public int value();
}
