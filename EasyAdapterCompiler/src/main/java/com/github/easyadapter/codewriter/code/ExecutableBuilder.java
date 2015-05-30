package com.github.easyadapter.codewriter.code;

import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.VariableGenerator;

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
