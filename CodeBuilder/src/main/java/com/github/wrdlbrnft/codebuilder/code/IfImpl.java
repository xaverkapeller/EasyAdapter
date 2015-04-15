package com.github.wrdlbrnft.codebuilder.code;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
class IfImpl implements If {

    private final CodeBlock mTrueBlock = new CodeBlockImpl();
    private final CodeBlock mFalseBlock = new CodeBlockImpl();
    private final CodeBlock mComparison = new CodeBlockImpl();

    public CodeBlock comparison() {
        return mComparison;
    }

    public CodeBlock whenTrue() {
        return mTrueBlock;
    }

    public CodeBlock whenFalse() {
        return mFalseBlock;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        write(builder);
        return builder.toString();
    }

    @Override
    public void write(StringBuilder builder) {
        if (mTrueBlock.isEmpty() && !mFalseBlock.isEmpty()) {
            builder.append("if(!(").append(mComparison).append(")) {\n");
            builder.append(mFalseBlock);
            builder.append("}\n");
        } else if (!mTrueBlock.isEmpty() && mFalseBlock.isEmpty()) {
            builder.append("if(").append(mComparison).append(") {\n");
            builder.append(mTrueBlock);
            builder.append("}\n");
        } else if (!mTrueBlock.isEmpty()) {
            builder.append("if(").append(mComparison).append(") {\n");
            builder.append(mTrueBlock);
            builder.append("} else {\n");
            builder.append(mFalseBlock);
            builder.append("}\n");
        }
    }
}
