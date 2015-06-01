package com.github.easyadapter.codewriter.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 30/11/14
 */
public class Utils {

    public static String getFullClassName(TypeElement element) {
        return element.asType().toString();
    }

    public static String getPackageName(Element element) {
        final Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement instanceof PackageElement) {
            final PackageElement packageElement = (PackageElement) enclosingElement;
            return packageElement.getQualifiedName().toString();
        } else {
            return getPackageName(enclosingElement) + "." + enclosingElement.getSimpleName().toString();
        }
    }

    public static AnnotationValue getAnnotationValue(Element element, String className, String name) {
        final Map<? extends ExecutableElement, ? extends AnnotationValue> map = getAnnotationElementValueMap(element, className);
        if (map != null) {
            return findAnnotationValue(name, map);
        }

        return null;
    }

    private static Map<? extends ExecutableElement, ? extends AnnotationValue> getAnnotationElementValueMap(Element element, String className) {
        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror mirror : annotationMirrors) {
            final String annotationClassName = mirror.getAnnotationType().toString();
            if (annotationClassName.equals(className)) {
                return mirror.getElementValues();
            }
        }

        return null;
    }

    private static AnnotationValue findAnnotationValue(String name, Map<? extends ExecutableElement, ? extends AnnotationValue> map) {
        for (ExecutableElement key : map.keySet()) {
            if (key.getSimpleName().toString().equals(name)) {
                return map.get(key);
            }
        }
        return null;
    }

    public static boolean hasAnnotation(Element element, String annotationClass) {
        final List<? extends AnnotationMirror> mirrors = element.getAnnotationMirrors();
        for (AnnotationMirror mirror : mirrors) {
            if (mirror.getAnnotationType().toString().equals(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    public static TypeElement getSuperElement(TypeElement element) {
        final TypeMirror superClassMirror = element.getSuperclass();
        if (superClassMirror == null || superClassMirror instanceof NoType) {
            return null;
        }

        final DeclaredType declaredType = (DeclaredType) superClassMirror;
        return (TypeElement) declaredType.asElement();
    }

    public static List<ExecutableElement> getConstructors(TypeElement typeElement) {
        final List<ExecutableElement> constructors = new ArrayList<>();

        final List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                final ExecutableElement constructor = (ExecutableElement) element;
                constructors.add(constructor);
            }
        }

        return constructors;
    }
}
