package com.github.easyadapter.codewriter.code;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 22/02/15
 */
public interface Switch<T> extends Writeable {
    public CodeBlock forCase(T item);
    public CodeBlock forDefault();
}
