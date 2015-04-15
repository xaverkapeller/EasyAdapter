package com.github.wrdlbrnft.codebuilder.code;

import com.github.wrdlbrnft.codebuilder.elements.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public interface CodeBlock extends Writeable {

    public CodeBlock append(String value);
    public CodeBlock append(int value);
    public CodeBlock append(long value);
    public CodeBlock append(float value);
    public CodeBlock append(double value);
    public CodeBlock append(CodeBlock code);
    public CodeBlock append(Object value);
    public CodeBlock append(Variable variable);
    public CodeBlock append(Writeable builder);
    public boolean isEmpty();
    public CodeBlock appendStatement(String statement);
    public If newIf();
    public If newIf(String comparison);
    public <T> Switch<T> newSwitch(Variable variable);

    public static class Factory {

        public static CodeBlock newInstance() {
            return new CodeBlockImpl();
        }
    }
}
