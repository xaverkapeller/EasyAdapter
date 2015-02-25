package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.code.BlockBuilder;
import com.github.easyadapter.builder.api.code.CodeBlock;
import com.github.easyadapter.builder.api.code.If;
import com.github.easyadapter.builder.api.code.Switch;
import com.github.easyadapter.builder.api.elements.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class CodeBlockImpl implements CodeBlock {

    final List<BlockBuilderImpl> mParts = new ArrayList<BlockBuilderImpl>();

    @Override
    public CodeBlock append(String value) {
        mParts.add(new BlockBuilderImpl(value));
        return this;
    }

    @Override
    public CodeBlock append(int value) {
        mParts.add(new BlockBuilderImpl(String.valueOf(value)));
        return this;
    }

    @Override
    public CodeBlock append(long value) {
        mParts.add(new BlockBuilderImpl(String.valueOf(value)));
        return this;
    }

    @Override
    public CodeBlock append(float value) {
        mParts.add(new BlockBuilderImpl(String.valueOf(value)));
        return this;
    }

    @Override
    public CodeBlock append(double value) {
        mParts.add(new BlockBuilderImpl(String.valueOf(value)));
        return this;
    }

    @Override
    public CodeBlock append(CodeBlock code) {
        mParts.add(new BlockBuilderImpl(code));
        return this;
    }

    @Override
    public CodeBlock append(Variable variable) {
        mParts.add(new BlockBuilderImpl(variable.name()));
        return this;
    }

    @Override
    public CodeBlock append(Object value) {
        mParts.add(new BlockBuilderImpl(String.valueOf(value)));
        return this;
    }

    @Override
    public CodeBlock appendStatement(String statement) {
        mParts.add(new BlockBuilderImpl(String.format("%s;\n", statement)));
        return this;
    }

    @Override
    public If newIf() {
        final IfImpl stub = new IfImpl();
        mParts.add(new BlockBuilderImpl(stub));
        return stub;
    }

    @Override
    public If newIf(String comparison) {
        final IfImpl stub = new IfImpl();
        mParts.add(new BlockBuilderImpl(stub));
        stub.comparison().append(comparison);
        return stub;
    }

    @Override
    public <T> Switch<T> newSwitch(Variable variable) {
        final SwitchImpl<T> stub = new SwitchImpl<>(variable);
        mParts.add(new BlockBuilderImpl(stub));
        return stub;
    }

    @Override
    public CodeBlock append(BlockBuilder builder) {
        mParts.add(new BlockBuilderImpl(builder));
        return this;
    }

    @Override
    public boolean isEmpty() {
        return mParts.isEmpty();
    }

    @Override
    public void write(StringBuilder builder) {
        for(BlockBuilderImpl blockBuilder : mParts) {
            blockBuilder.write(builder);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        write(builder);
        return builder.toString();
    }
}
