package com.github.easyadapter.builder.api.code;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
public interface If extends BlockBuilder {
    public CodeBlock comparison();
    public CodeBlock whenTrue();
    public CodeBlock whenFalse();
}
