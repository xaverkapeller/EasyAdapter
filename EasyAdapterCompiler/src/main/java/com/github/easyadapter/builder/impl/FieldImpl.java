package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.elements.Field;
import com.github.easyadapter.builder.api.elements.Type;

import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
class FieldImpl extends VariableImpl implements Field {

    private Type mType;
    private String mInitialValue = null;

    public FieldImpl(String name, Type type, Set<Modifier> modifiers) {
        super(name, type, modifiers);

        mType = type;
    }

    @Override
    public void setInitialValue(String value) {
        mInitialValue = value;
    }

    @Override
    public Type type() {
        return mType;
    }

    public String initialValue() {
        return mInitialValue;
    }
}
