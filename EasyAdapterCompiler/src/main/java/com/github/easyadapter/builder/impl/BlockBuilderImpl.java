package com.github.easyadapter.builder.impl;

import com.github.easyadapter.builder.api.code.BlockBuilder;
import com.github.easyadapter.builder.api.code.CodeBlock;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class BlockBuilderImpl implements BlockBuilder {

    private final BlockBuilder mBlockBuilder;

    public BlockBuilderImpl(final String code) {
        mBlockBuilder = new BlockBuilder() {
            @Override
            public void write(StringBuilder builder) {
                builder.append(code);
            }
        };
    }

    public BlockBuilderImpl(BlockBuilder builder) {
        mBlockBuilder = builder;
    }

    public BlockBuilderImpl(final CodeBlock codeBlock) {
        mBlockBuilder = new BlockBuilder() {
            @Override
            public void write(StringBuilder builder) {
                builder.append(codeBlock.toString());
            }
        };
    }

    @Override
    public void write(StringBuilder builder) {
        mBlockBuilder.write(builder);
    }
}
