package com.github.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Defines the layout resource id of the layout which will be used for the view model.
 * Only classes which implement {@link com.github.easyadapter.api.ViewModel} can be annotated with
 * this annotation. Annotating another class with this will yield a compile time error.
 */
@Target(ElementType.TYPE)
public @interface Layout {
    public int value();
}
