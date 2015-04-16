package com.github.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Defines the id which identifies the {@link android.view.View} which needs to be injected into
 * the annotated parameter.
 */
@Target(ElementType.PARAMETER)
public @interface InjectView {
    public int value();
}
