package com.github.easyadapter.api;

import java.text.DateFormat;
import java.util.Date;

/**
 * <p>
 *     Used by {@link com.github.easyadapter.annotations.BindToView} to format or transform return
 *     values of method so they can be bound to a {@link android.view.View}.
 * </p>
 * <p>
 *     The return type of the method has to be assignable to the input type parameter {@link I}. The
 *     output type parameter {@link O} will has to be assignable to the target property of the
 *     {@link android.view.View}.
 * </p>
 */
public interface Formater<I, O> {
    public O format(I input);

    public static class DateToString implements Formater<Date, String> {

        private static final DateFormat dateFormat = DateFormat.getDateInstance();

        @Override
        public String format(Date input) {
            return dateFormat.format(input);
        }
    }

    public static class Default implements Formater<Object, Object> {

        @Override
        public Object format(Object input) {
            return input;
        }
    }
}
