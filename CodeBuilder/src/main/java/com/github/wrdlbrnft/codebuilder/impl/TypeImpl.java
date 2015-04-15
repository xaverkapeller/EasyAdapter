package com.github.wrdlbrnft.codebuilder.impl;

import com.github.wrdlbrnft.codebuilder.elements.Type;
import com.github.wrdlbrnft.codebuilder.elements.Variable;

import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class TypeImpl implements Type {

    private final String mPackageName;
    private final String mClassName;
    private final String mFullClassName;
    private final Set<Modifier> mModifiers;
    private final Type mParent;

    private TypeElement element;

    public TypeImpl(String packageName, String className, Set<Modifier> modifiers, Type parent) {
        mPackageName = packageName;
        mClassName = className;
        mFullClassName = packageName != null && packageName.length() > 0 ? String.format("%s.%s", packageName, className) : className;
        mModifiers = modifiers;
        mParent = parent;
    }

    public TypeElement getTypeElement() {
        return this.element;
    }

    public void setTypeElement(TypeElement element) {
        this.element = element;
    }

    @Override
    public String packageName() {
        return mPackageName;
    }

    @Override
    public String className() {
        return mClassName;
    }

    @Override
    public String fullClassName() {
        return mFullClassName;
    }

    @Override
    public Type parent() {
        return mParent;
    }

    @Override
    public Set<Modifier> modifiers() {
        return mModifiers;
    }

    @Override
    public boolean isSubTypeOf(Type type) {
        return this.equals(type) || mParent != null && mParent.isSubTypeOf(type);
    }

    @Override
    public Type genericVersion(Type... genericTypes) {
        final StringBuilder builder = new StringBuilder();

        final int count = genericTypes.length;
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final Type type = genericTypes[i];

                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(type.className());
            }
        } else {
            builder.append("?");
        }

        final String genericClassName = String.format("%s<%s>", mClassName, builder.toString());
        return new TypeImpl(mPackageName, genericClassName, mModifiers, mParent);
    }

    @Override
    public Type nonGenericVersion() {
        final int genericIndex = mClassName.indexOf("<");
        if (genericIndex >= 0) {
            return new TypeImpl(mPackageName, mClassName.substring(0, genericIndex), mModifiers, mParent);
        }

        return this;
    }

    @Override
    public String newInstance(String... parameters) {
        final StringBuilder builder = new StringBuilder();
        builder.append("new ").append(mClassName).append("(");

        for (int i = 0, count = parameters.length; i < count; i++) {
            final String parameter = parameters[i];

            if (i > 0) {
                builder.append(", ");
            }

            builder.append(parameter);
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String newInstance(Variable... parameters) {
        final StringBuilder builder = new StringBuilder();
        builder.append("new ").append(mClassName).append("(");

        for (int i = 0, count = parameters.length; i < count; i++) {
            final Variable parameter = parameters[i];

            if (i > 0) {
                builder.append(", ");
            }

            builder.append(parameter.name());
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String toClass() {
        return mClassName + ".class";
    }

    @Override
    public String toString() {
        return mClassName;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Type) {
            final Type otherType = (Type) obj;
            final Type a = this.nonGenericVersion();
            final Type b = otherType.nonGenericVersion();
            return a.fullClassName().trim().equals(b.fullClassName().trim());
        }

        return false;
    }
}
