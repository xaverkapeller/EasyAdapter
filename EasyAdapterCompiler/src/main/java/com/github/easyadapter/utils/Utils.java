package com.github.easyadapter.utils;

import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.EnumSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 30/11/14
 */
public class Utils {

    /**
     * Tests wheter the given {@link javax.lang.model.element.Element} is a class.
     * @param element The {@link javax.lang.model.element.Element} in question.
     * @return true if the {@link javax.lang.model.element.Element} is a class, otherwise false.
     */
    public static boolean isClass(Element element) {
        return element.getKind() == ElementKind.CLASS;
    }

    /**
     * Tests wheter the given {@link javax.lang.model.element.Element} is a method.
     * @param element The {@link javax.lang.model.element.Element} in question.
     * @return true if the {@link javax.lang.model.element.Element} is a method, otherwise false.
     */
    public static boolean isMethod(Element element) {
        return element.getKind() == ElementKind.METHOD;
    }

    /**
     * Tests wheter the given {@link javax.lang.model.element.Element} is a interface.
     * @param element The {@link javax.lang.model.element.Element} in question.
     * @return true if the {@link javax.lang.model.element.Element} is a interface, otherwise false.
     */
    public static boolean isInterface(Element element) {
        return element.getKind() == ElementKind.INTERFACE;
    }

    /**
     * Tests wheter the given {@link javax.lang.model.element.Element} is a field.
     * @param element The {@link javax.lang.model.element.Element} in question.
     * @return true if the {@link javax.lang.model.element.Element} is a field, otherwise false.
     */
    public static boolean isField(Element element) {
        return element.getKind() == ElementKind.FIELD;
    }

    /**
     * Returns the fully qualified class name of the supplied {@link javax.lang.model.element.Element}.
     * @param element The {@link javax.lang.model.element.Element} in question
     * @return The fully qualified class name as {@link String}
     */
    public static String getFullClassName(TypeElement element) {
        final String packageName = getPackageName(element);
        final String className = element.getSimpleName().toString();
        return String.format("%s.%s", packageName, className);
    }

    /**
     * Returns the package name of the supplied {@link javax.lang.model.element.Element}.
     * @param element The {@link javax.lang.model.element.Element} in question
     * @return The package name of the {@link javax.lang.model.element.Element}
     */
    public static String getPackageName(Element element) {
        final Element enclosingElement = element.getEnclosingElement();
        if(enclosingElement instanceof PackageElement) {
            final PackageElement packageElement = (PackageElement) enclosingElement;
            return packageElement.getQualifiedName().toString();
        } else {
            return getPackageName(enclosingElement) + "." + enclosingElement.getSimpleName();
        }
    }

    /**
     * Creates a new {@link com.squareup.javawriter.JavaWriter} in the current {@link javax.annotation.processing.ProcessingEnvironment}.
     * @param environment The current {@link javax.annotation.processing.ProcessingEnvironment}
     * @param newSourceName The name of the new Java file
     * @return A new {@link com.squareup.javawriter.JavaWriter}
     * @throws java.io.IOException
     */
    public static JavaWriter createWriter(ProcessingEnvironment environment, String newSourceName) throws IOException {
        final JavaFileObject sourceFile = environment.getFiler().createSourceFile(newSourceName);
        return new JavaWriter(sourceFile.openWriter());
    }

    /**
     * Raises a compile error and indicates a supplied {@link javax.lang.model.element.Element} as source of the error.
     * @param environment The current {@link javax.annotation.processing.ProcessingEnvironment} in which the error will be raised
     * @param message The message to go along with the error
     * @param element The reference {@link javax.lang.model.element.Element} for the error.
     */
    public static void error(ProcessingEnvironment environment, String message, Element element) {
        environment.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    /**
     * Raises a compile error
     * @param environment The current {@link javax.annotation.processing.ProcessingEnvironment} in which the error will be raised
     * @param message The message to go along with the error
     */
    public static void error(ProcessingEnvironment environment, String message) {
        environment.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }

    public static TypeElement getSuperElement(TypeElement element) {
        final TypeMirror superClassMirror = element.getSuperclass();
        if (superClassMirror == null || superClassMirror instanceof NoType) {
            return null;
        }

        final DeclaredType declaredType = (DeclaredType) superClassMirror;
        return (TypeElement) declaredType.asElement();
    }

    public static String getClassName(Class<?> cls, Class<?> genericType) {
        return String.format("%s<%s>", cls.getName(), genericType.getName());
    }

    public static String getClassName(Class<?> cls) {
        return cls.getName();
    }
}
