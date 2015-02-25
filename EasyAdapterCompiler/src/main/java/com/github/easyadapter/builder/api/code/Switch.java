package com.github.easyadapter.builder.api.code;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 22/02/15
 */
public interface Switch<T> extends BlockBuilder {
    public CodeBlock forCase(T item);
    public CodeBlock forDefault();
}
