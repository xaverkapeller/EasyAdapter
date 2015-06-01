package com.github.easyadapter.processor.builder.exceptions;

import com.github.easyadapter.codewriter.elements.Type;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 30/05/15
 */
public class ViewTypeMismatchException extends Exception {

    private Type typeA;
    private Type typeB;

    public ViewTypeMismatchException(String message, Type typeA, Type typeB) {
        super(message);

        this.typeA = typeA;
        this.typeB = typeB;
    }

    public ViewTypeMismatchException(String message, Throwable cause, Type typeA, Type typeB) {
        super(message, cause);

        this.typeA = typeA;
        this.typeB = typeB;
    }

    public ViewTypeMismatchException(Throwable cause, Type typeA, Type typeB) {
        super(cause);

        this.typeA = typeA;
        this.typeB = typeB;
    }

    public Type getTypeA() {
        return typeA;
    }

    public Type getTypeB() {
        return typeB;
    }
}
