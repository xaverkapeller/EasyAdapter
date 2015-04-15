package com.github.wrdlbrnft.codebuilder.impl;

import com.github.wrdlbrnft.codebuilder.elements.Constructor;
import com.github.wrdlbrnft.codebuilder.elements.Variable;

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
