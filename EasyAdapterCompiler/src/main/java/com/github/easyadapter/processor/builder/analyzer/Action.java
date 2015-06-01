package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.VariableGenerator;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 30/05/15
 */
public interface Action {
    public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator);
}
