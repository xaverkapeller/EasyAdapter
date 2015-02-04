package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.builder.ExecutableBuilder;
import com.github.easyadapter.builder.api.elements.Method;
import com.github.easyadapter.builder.api.elements.Type;
import com.github.easyadapter.builder.api.elements.Variable;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/02/15
 */
public class Methods {

    public static Method create(Type returnType, String name, List<Variable> parameters, Set<Modifier> modifiers) {
        return new MethodImpl(returnType, name, parameters, modifiers);
    }

    public static Method create(Type returnType, String name, Set<Modifier> modifiers, ExecutableBuilder builder) {
        final VariableGenerator generator = new VariableGenerator();
        final List<Variable> parameters = builder.createParameterList(generator);
        final MethodImpl method = new MethodImpl(returnType, name, parameters, modifiers);
        builder.writeBody(method.code(), generator);
        return method;
    }
}
