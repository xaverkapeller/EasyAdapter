package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.code.CodeBlock;
import com.github.easyadapter.builder.api.code.Switch;
import com.github.easyadapter.builder.api.elements.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 22/02/15
 */
class SwitchImpl<T> implements Switch<T> {

    private final Map<T, CodeBlock> mCaseMap = new HashMap<>();
    private final Variable mVariable;

    private CodeBlock mDefaultBlock = null;



    public SwitchImpl(Variable variable) {
        mVariable = variable;
    }

    @Override
    public CodeBlock forCase(T item) {
        if(!mCaseMap.containsKey(item)) {
            mCaseMap.put(item, new CodeBlockImpl());
        }

        return mCaseMap.get(item);
    }

    @Override
    public CodeBlock forDefault() {
        if(mDefaultBlock == null) {
            mDefaultBlock = new CodeBlockImpl();
        }

        return mDefaultBlock;
    }

    @Override
    public void write(StringBuilder builder) {
        builder.append("switch(").append(mVariable.name()).append(") {\n");

        final Set<T> caseSet = mCaseMap.keySet();
        for(T item : caseSet) {
            final CodeBlock caseBlock = mCaseMap.get(item);

            builder.append("case ").append(String.valueOf(item)).append(":\n");
            caseBlock.write(builder);
            builder.append("break;\n");
        }

        if(mDefaultBlock != null) {
            builder.append("default:\n");
            mDefaultBlock.write(builder);
            builder.append("break;\n");
        }

        builder.append("}\n");
    }
}
