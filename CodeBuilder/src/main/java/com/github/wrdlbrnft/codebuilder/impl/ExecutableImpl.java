package com.github.wrdlbrnft.codebuilder.impl;

import com.github.wrdlbrnft.codebuilder.code.CodeBlock;
import com.github.wrdlbrnft.codebuilder.elements.Executable;
import com.github.wrdlbrnft.codebuilder.elements.Variable;

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
    private final CodeBlock mCode = CodeBlock.Factory.newInstance();

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
