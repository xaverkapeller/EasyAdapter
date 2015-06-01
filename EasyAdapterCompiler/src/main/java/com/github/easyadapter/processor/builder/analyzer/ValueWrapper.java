package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.elements.Type;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
class ValueWrapper {
    public final String value;
    public final Type type;

    ValueWrapper(String value, Type type) {
        this.value = value;
        this.type = type;
    }
}
