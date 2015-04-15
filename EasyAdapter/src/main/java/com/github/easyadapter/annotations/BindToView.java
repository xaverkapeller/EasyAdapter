package com.github.easyadapter.annotations;

import android.view.View;

import com.github.easyadapter.api.Formater;
import com.github.easyadapter.api.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 29/01/15
 */
@Target(ElementType.METHOD)
public @interface BindToView {
    public int[] value();
    public Property property() default Property.AUTO;
    public Class<? extends Formater<?, ?>> formater() default Formater.Default.class;
}
