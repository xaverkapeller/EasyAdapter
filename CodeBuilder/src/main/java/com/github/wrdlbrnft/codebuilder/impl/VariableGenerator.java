package com.github.wrdlbrnft.codebuilder.impl;

import com.github.wrdlbrnft.codebuilder.elements.Type;
import com.github.wrdlbrnft.codebuilder.elements.Variable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 21/12/14
 */
public class VariableGenerator {

    public static Set<Variable> generateParameters(Type... types) {
        final VariableGenerator generator = new VariableGenerator();
        final Set<Variable> parameters = new HashSet<Variable>();

        for(Type type : types) {
            parameters.add(generator.generate(type, new HashSet<Modifier>()));
        }

        return parameters;
    }

    private static final Character[] CHARACTERS = new Character[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z'
    };

    private int mVariableCount = 0;
    private String mPrefix = null;
    private String mPostfix = null;

    private String getNameForIndex(int index) {
        final StringBuilder builder = new StringBuilder();
        final int characterCount = index / CHARACTERS.length;

        if (mPrefix != null) {
            builder.append(mPrefix);
        }

        for (int i = 0; i < characterCount + 1; i++) {
            final Character c = CHARACTERS[index % CHARACTERS.length];
            builder.append(c);
            index -= CHARACTERS.length;
        }

        if (mPostfix != null) {
            builder.append(mPostfix);
        }

        return builder.toString();
    }

    public void setPrefix(String prefix) {
        mPrefix = prefix;
    }

    public void setPostfix(String postfix) {
        mPostfix = postfix;
    }

    public String generateName() {
        final int index = mVariableCount++;
        return getNameForIndex(index);
    }

    public Variable generate(Type type, Set<Modifier> modifiers) {
        final String name = generateName();
        return new VariableImpl(name, type, modifiers);
    }

    public Variable generate(Type type, Modifier... modifiers) {
        final Set<Modifier> modifierSet = new HashSet<Modifier>();
        Collections.addAll(modifierSet, modifiers);
        return generate(type, modifierSet);
    }

    public Variable generate(Class<?> cls, Modifier... modifiers) {
        final Set<Modifier> modifierSet = new HashSet<Modifier>();
        Collections.addAll(modifierSet, modifiers);
        return generate(cls, modifierSet);
    }

    public Variable generate(Class<?> cls, Set<Modifier> modifiers) {
        final Type type = Types.create(cls);
        return generate(type, modifiers);
    }
}
