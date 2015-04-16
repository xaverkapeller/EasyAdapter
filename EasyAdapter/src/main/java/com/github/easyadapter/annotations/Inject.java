package com.github.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates to the {@link com.github.easyadapter.EasyAdapter} that the annotated Parameter
 * has to be injected.
 */
@Target(ElementType.PARAMETER)
public @interface Inject {
}
