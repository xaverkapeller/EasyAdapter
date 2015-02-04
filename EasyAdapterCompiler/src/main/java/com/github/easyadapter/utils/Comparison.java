package com.github.easyadapter.utils;

import com.github.easyadapter.builder.api.elements.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
public class Comparison {

    public enum Operator {
        EQUALS("=="),
        NOT_EQUALS("!=");

        private final String mStringRepresentation;

        private Operator(String operator) {
            mStringRepresentation = operator;
        }

        @Override
        public String toString() {
            return mStringRepresentation;
        }
    }

    public static String compare(Variable a, Operator operator, Variable b) {
        return compare(a.name(), operator, b.name());
    }

    public static String compare(String a, Operator operator, Variable b) {
        return compare(a, operator, b.name());
    }

    public static String compare(Variable a, Operator operator, String b) {
        return compare(a.name(), operator, b);
    }

    public static String compare(String a, Operator operator, String b) {
        return String.format("%s %s %s", a, operator, b);
    }
}
