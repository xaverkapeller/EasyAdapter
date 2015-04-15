package com.github.wrdlbrnft.codebuilder.impl;

import com.github.wrdlbrnft.codebuilder.elements.Method;
import com.github.wrdlbrnft.codebuilder.elements.Type;
import com.github.wrdlbrnft.codebuilder.elements.Variable;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class MethodImpl extends ExecutableImpl implements Method {

    private final String mName;
    private final Type mReturnType;

    public MethodImpl(Type returnType, String name, List<Variable> parameters, Set<Modifier> modifiers) {
        super(parameters, modifiers);
        mReturnType = returnType;
        mName = name;
    }

    @Override
    public String name() {
        return mName;
    }

    @Override
    public Type returnType() {
        return mReturnType;
    }

    @Override
    public String execute(String target, Variable... parameters) {
        final List<Variable> methodParameters = parameters();
        if (methodParameters.size() != parameters.length) {
            throw new IllegalStateException("Supplied too many or too few arguments for method " + mName);
        }

        final StringBuilder builder = new StringBuilder();

        if(target != null) {
            builder.append(target).append(".");
        }

        builder.append(mName).append("(");

        for (int i = 0, length = methodParameters.size(); i < length; i++) {
            if (i > 0) {
                builder.append(", ");
            }

            final Variable methodParameter = methodParameters.get(i);
            final Variable suppliedParameter = parameters[i];

            if (suppliedParameter.type().isSubTypeOf(methodParameter.type())) {
                builder.append(suppliedParameter.name());
            } else {
                throw new IllegalStateException("The supplied parameter " + suppliedParameter.name() + " cannot be applied to " + methodParameter.type().fullClassName());
            }
        }

        builder.append(")");

        return builder.toString();
    }

    @Override
    public String execute(String target, String... parameters) {
        final List<Variable> methodParameters = parameters();
        if (methodParameters.size() != parameters.length) {
            throw new IllegalStateException("Supplied too many or too few arguments for method " + mName);
        }

        final StringBuilder builder = new StringBuilder();

        if(target != null) {
            builder.append(target).append(".");
        }

        builder.append(mName).append("(");

        for (int i = 0, length = parameters.length; i < length; i++) {
            if (i > 0) {
                builder.append(", ");
            }

            final String suppliedParameter = parameters[i];

            builder.append(suppliedParameter);
        }

        builder.append(")");

        return builder.toString();
    }
}
