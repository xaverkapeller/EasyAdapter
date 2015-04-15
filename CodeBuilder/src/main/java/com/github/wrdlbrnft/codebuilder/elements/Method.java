package com.github.wrdlbrnft.codebuilder.elements;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public interface Method extends Executable {
    public String name();
    public Type returnType();
    public String execute(String target, Variable... parameters);
    public String execute(String target, String... parameters);
}
