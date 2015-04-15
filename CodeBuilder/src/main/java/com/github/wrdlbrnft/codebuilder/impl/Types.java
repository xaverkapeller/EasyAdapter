package com.github.wrdlbrnft.codebuilder.impl;

import com.github.wrdlbrnft.codebuilder.elements.Type;
import com.github.wrdlbrnft.codebuilder.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public class Types {

    public static final Type OBJECT = create("java.lang", "Object", EnumSet.of(Modifier.PUBLIC), null);
    public static final Type LIST = create("java.util", "List", EnumSet.of(Modifier.PUBLIC), OBJECT);
    public static final Type MAP = create("java.util", "Map", EnumSet.of(Modifier.PUBLIC), OBJECT);
    public static final Type CLASS = create("java.lang", "Class", EnumSet.of(Modifier.PUBLIC), OBJECT);
    public static final Type STRING = create(String.class);
    public static final Type DATE = create(Date.class);
    public static final Type CALENDAR = create(Calendar.class);

    public static final Type[] PRIMITIVE_TYPES = new Type[] {
            Primitives.VOID,
            Primitives.FLOAT,
            Primitives.INTEGER,
            Primitives.LONG,
            Primitives.BOOLEAN,
            Primitives.DOUBLE,
    };

    public static boolean isPrimitive(Type type) {
        for(Type primitiveType : PRIMITIVE_TYPES) {
            if(primitiveType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static class Primitives {

        public static final Type VOID = create(void.class);
        public static final Type FLOAT = create(float.class);
        public static final Type INTEGER = create(int.class);
        public static final Type LONG = create(long.class);
        public static final Type BOOLEAN = create(boolean.class);
        public static final Type DOUBLE = create(double.class);
    }

    public static class Boxed {

        public static final Type VOID = create(Void.class);
        public static final Type FLOAT = create(Float.class);
        public static final Type INTEGER = create(Integer.class);
        public static final Type LONG = create(Long.class);
        public static final Type BOOLEAN = create(Boolean.class);
        public static final Type DOUBLE = create(Double.class);
    }

    public static class Android {

        public static final Type CONTEXT = create("android.content", "Context", EnumSet.of(Modifier.PUBLIC), OBJECT);
        public static final Type RECYCLERVIEW_VIEWHOLDER = create("android.support.v7.widget.RecyclerView", "ViewHolder", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), OBJECT);
        public static final Type SHARED_PREFERENCES = create("android.content", "SharedPreferences", EnumSet.of(Modifier.PUBLIC), OBJECT);
        public static final Type LAYOUT_INFLATER = create("android.view", "LayoutInflater", EnumSet.of(Modifier.PUBLIC), OBJECT);
        public static final Type DRAWABLE = create("android.graphics.drawable", "Drawable", EnumSet.of(Modifier.PUBLIC), OBJECT);

        public static class Views {

            public static final Type VIEW = create("android.view", "View", EnumSet.of(Modifier.PUBLIC), OBJECT);
            public static final Type TEXTVIEW = create("android.widget", "TextView", EnumSet.of(Modifier.PUBLIC), VIEW);
            public static final Type BUTTON = create("android.widget", "Button", EnumSet.of(Modifier.PUBLIC), TEXTVIEW);
            public static final Type COMPOUND_BUTTON = create("android.widget", "Button", EnumSet.of(Modifier.PUBLIC), BUTTON);
            public static final Type CHECKBOX = create("android.widget", "CheckBox", EnumSet.of(Modifier.PUBLIC), COMPOUND_BUTTON);
            public static final Type IMAGEVIEW = create("android.widget", "ImageView", EnumSet.of(Modifier.PUBLIC), VIEW);
            public static final Type VIEW_GROUP = create("android.view", "ViewGroup", EnumSet.of(Modifier.PUBLIC), VIEW);
        }
    }

    public static Type create(String packageName, String className, Set<Modifier> modifiers, Type parent) {
        return new TypeImpl(packageName, className, modifiers, parent);
    }

    public static Type create(TypeElement element) {
        final String className = element.getSimpleName().toString();
        final String packageName = Utils.getPackageName(element);
        final Set<Modifier> modifiers = element.getModifiers();
        final TypeElement superElement = Utils.getSuperElement(element);
        final Type superType = superElement != null ? create(superElement) : null;
        final TypeImpl resultType = new TypeImpl(packageName, className, modifiers, superType);
        resultType.setTypeElement(element);
        return resultType;
    }

    public static Type create(Class<?> cls) {
        final String className = cls.getSimpleName();
        final Package classPackage = cls.getPackage();
        final String packageName = classPackage != null ? classPackage.getName() : "";
        final Set<Modifier> modifiers = getModifiersFromClass(cls);
        final Class<?> superClass = cls.getSuperclass();
        final Type superType = superClass != null ? create(superClass) : null;
        return create(packageName, className, modifiers, superType);
    }

    public static Type create(TypeMirror type) {

        if (type instanceof NoType) {
            return Primitives.VOID;
        }

        if (type instanceof DeclaredType) {
            final DeclaredType declaredType = (DeclaredType) type;
            final TypeElement element = (TypeElement) declaredType.asElement();
            return create(element);
        }

        final String fullClassName = type.toString();
        final int index = fullClassName.lastIndexOf(".");
        if (index >= 0) {
            final String packageName = fullClassName.substring(0, index);
            final String className = fullClassName.substring(index + 1, fullClassName.length() - 1);
            return create(packageName, className, new HashSet<Modifier>(), OBJECT);
        }

        return create("", type.toString(), new HashSet<Modifier>(), OBJECT);
    }

    private static Set<Modifier> getModifiersFromClass(Class<?> cls) {
        final int modifiers = cls.getModifiers();
        return decodeModifiers(modifiers);
    }

    private static Set<Modifier> decodeModifiers(int modifiers) {
        final Set<Modifier> modifierSet = new HashSet<Modifier>();

        if (java.lang.reflect.Modifier.isAbstract(modifiers)) {
            modifierSet.add(Modifier.ABSTRACT);
        }

        if (java.lang.reflect.Modifier.isFinal(modifiers)) {
            modifierSet.add(Modifier.FINAL);
        }

        if (java.lang.reflect.Modifier.isNative(modifiers)) {
            modifierSet.add(Modifier.NATIVE);
        }

        if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
            modifierSet.add(Modifier.PRIVATE);
        }

        if (java.lang.reflect.Modifier.isProtected(modifiers)) {
            modifierSet.add(Modifier.PROTECTED);
        }

        if (java.lang.reflect.Modifier.isPublic(modifiers)) {
            modifierSet.add(Modifier.PUBLIC);
        }

        if (java.lang.reflect.Modifier.isStatic(modifiers)) {
            modifierSet.add(Modifier.STATIC);
        }

        if (java.lang.reflect.Modifier.isSynchronized(modifiers)) {
            modifierSet.add(Modifier.SYNCHRONIZED);
        }

        if (java.lang.reflect.Modifier.isTransient(modifiers)) {
            modifierSet.add(Modifier.TRANSIENT);
        }

        return modifierSet;
    }
}
