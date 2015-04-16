package com.github.easyadapter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Defines that the annotated method will be called when the view model is unbound from a
 * {@link com.github.easyadapter.impl.AbsViewHolder}. Parameters of the annotated method will be
 * satisfied if possible, see {@link com.github.easyadapter.annotations.Inject} and
 * {@link com.github.easyadapter.annotations.InjectView}.
 */
@Target(ElementType.METHOD)
public @interface OnUnbind {
}
