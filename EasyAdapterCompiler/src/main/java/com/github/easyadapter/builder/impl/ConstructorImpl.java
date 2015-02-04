package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.elements.Constructor;
import com.github.easyadapter.builder.api.elements.Variable;

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
