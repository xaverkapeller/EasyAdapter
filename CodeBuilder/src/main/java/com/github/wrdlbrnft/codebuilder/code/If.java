package com.github.wrdlbrnft.codebuilder.code;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
public interface If extends Writeable {
    public CodeBlock comparison();
    public CodeBlock whenTrue();
    public CodeBlock whenFalse();
}
