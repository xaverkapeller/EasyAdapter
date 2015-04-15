package com.github.wrdlbrnft.codebuilder.impl;

import com.github.wrdlbrnft.codebuilder.code.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.elements.Method;
import com.github.wrdlbrnft.codebuilder.elements.Type;
import com.github.wrdlbrnft.codebuilder.elements.Variable;

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
