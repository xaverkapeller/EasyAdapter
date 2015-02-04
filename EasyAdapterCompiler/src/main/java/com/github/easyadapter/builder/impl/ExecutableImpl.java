package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.code.CodeBlock;
import com.github.easyadapter.builder.api.elements.Executable;
import com.github.easyadapter.builder.api.elements.Variable;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 21/12/14
 */
class ExecutableImpl implements Executable {

    private final List<Variable> mParameters;
    private final Set<Modifier> mModifiers;
    private final CodeBlockImpl mCode = new CodeBlockImpl();

    public ExecutableImpl(List<Variable> parameters, Set<Modifier> modifiers) {
        mParameters = parameters;
        mModifiers = modifiers;
    }

    @Override
    public List<Variable> parameters() {
        return mParameters;
    }

    @Override
    public Set<Modifier> modifiers() {
        return mModifiers;
    }

    @Override
    public CodeBlock code() {
        return mCode;
    }
}
