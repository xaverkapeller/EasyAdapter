package com.github.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Defines that the annotated method will be called when the {@link android.widget.CheckBox} which
 * is identified by the supplied id is either checked or unchecked. Parameters of the annotated
 * method will be satisfied if possible, see {@link com.github.easyadapter.annotations.Inject} and
 * {@link com.github.easyadapter.annotations.InjectView}.
 */
@Target(ElementType.METHOD)
public @interface OnCheckedChanged {
    public int value();
}
