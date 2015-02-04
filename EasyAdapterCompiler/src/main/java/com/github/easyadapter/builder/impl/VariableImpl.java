package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.elements.Type;
import com.github.easyadapter.builder.api.elements.Variable;

import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class VariableImpl implements Variable {

    private final String mName;
    private final Type mType;
    private final Set<Modifier> mModifiers;

    public VariableImpl(String name, Type type, Set<Modifier> modifiers) {
        mName = name;
        mType = type;
        mModifiers = modifiers;
    }

    @Override
    public String name() {
        return mName;
    }

    @Override
    public String set(String value) {
        return String.format("%s = %s", mName, value);
    }

    @Override
    public String set(Variable variable) {
        return String.format("%s = %s", mName, variable);
    }

    @Override
    public Type type() {
        return mType;
    }

    @Override
    public String initialize() {
        final StringBuilder builder = new StringBuilder();
        if(mModifiers.contains(Modifier.FINAL)) {
            builder.append("final ");
        }
        builder.append(mType.fullClassName()).append(" ");
        builder.append(mName);
        return builder.toString();
    }

    @Override
    public String cast(Type type) {
        return "(" + type.fullClassName() + ") " + mName;
    }

    @Override
    public Set<Modifier> modifiers() {
        return mModifiers;
    }

    @Override
    public String equalsTo(String value) {
        return String.format("%s == %s", mName, value);
    }

    @Override
    public String equalsTo(Variable variable) {
        return String.format("%s == %s", mName, variable.name());
    }

    @Override
    public String notEqualsTo(String value) {
        return String.format("%s != %s", mName, value);
    }

    @Override
    public String notEqualsTo(Variable variable) {
        return String.format("%s != %s", mName, variable.name());
    }

    @Override
    public String toString() {
        return mName;
    }
}
