package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.elements.Type;

import java.util.Set;

import javax.lang.model.element.Modifier;

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

    public TypeImpl(String packageName, String className, Set<Modifier> modifiers, Type parent) {
        mPackageName = packageName;
        mClassName = className;
        mFullClassName = packageName != null && packageName.length() > 0 ? String.format("%s.%s", packageName, className) : className;
        mModifiers = modifiers;
        mParent = parent;
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
    public boolean isSuperTypeOf(Type type) {
        return this.equals(type) || mParent != null && mParent.isSuperTypeOf(type);
    }

    @Override
    public Type genericVersion(Type... genericTypes) {
        final StringBuilder builder = new StringBuilder();

        final int count = genericTypes.length;
        if(count > 0) {
            for (int i = 0; i < count; i++) {
                final Type type = genericTypes[i];

                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(type.fullClassName());
            }
        } else {
            builder.append("?");
        }

        final String genericClassName = String.format("%s<%s>", mClassName, builder.toString());
        return new TypeImpl(mPackageName, genericClassName, mModifiers, mParent);
    }

    @Override
    public String toString() {
        return mFullClassName;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Type) {
            final Type otherType = (Type) obj;
            final String otherClassName = otherType.fullClassName();
            return mFullClassName.equals(otherClassName);
        }

        return false;
    }
}
