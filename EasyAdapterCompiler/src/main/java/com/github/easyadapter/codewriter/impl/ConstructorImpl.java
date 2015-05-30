package com.github.easyadapter.codewriter.impl;

import com.github.easyadapter.codewriter.elements.Constructor;
import com.github.easyadapter.codewriter.elements.Variable;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 21/12/14
 */
class ConstructorImpl extends ExecutableImpl implements Constructor {

    public ConstructorImpl(List<Variable> parameters, Set<Modifier> modifiers) {
        super(parameters, modifiers);
    }
}
