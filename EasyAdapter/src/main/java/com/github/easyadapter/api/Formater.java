package com.github.easyadapter.api;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by kapeller on 08/04/15.
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
