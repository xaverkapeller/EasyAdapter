package com.github.wrdlbrnft.codebuilder.elements;

import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public interface Type {
    public String packageName();
    public String className();
    public String fullClassName();
    public Type parent();
    public Set<Modifier> modifiers();
    public boolean isSubTypeOf(Type type);
    public Type genericVersion(Type... genericTypes);
    public Type nonGenericVersion();

    public TypeElement getTypeElement();

    public String newInstance(String... parameters);
    public String newInstance(Variable... parameters);
    public String toClass();
}
