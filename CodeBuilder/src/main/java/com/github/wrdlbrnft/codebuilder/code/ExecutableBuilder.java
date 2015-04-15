package com.github.wrdlbrnft.codebuilder.code;

import com.github.wrdlbrnft.codebuilder.elements.Variable;
import com.github.wrdlbrnft.codebuilder.impl.VariableGenerator;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
public interface ExecutableBuilder {
    public List<Variable> createParameterList(VariableGenerator generator);
    public void writeBody(CodeBlock code, VariableGenerator generator);
}
