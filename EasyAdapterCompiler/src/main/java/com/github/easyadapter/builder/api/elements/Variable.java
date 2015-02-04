package com.github.easyadapter.builder.api.elements;

import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public interface Variable {
    public String name();
    public String set(String value);
    public String set(Variable variable);
    public Type type();
    public String initialize();
    public String cast(Type type);
    public Set<Modifier> modifiers();
    public String equalsTo(String value);
    public String equalsTo(Variable variable);
    public String notEqualsTo(String value);
    public String notEqualsTo(Variable variable);
}
