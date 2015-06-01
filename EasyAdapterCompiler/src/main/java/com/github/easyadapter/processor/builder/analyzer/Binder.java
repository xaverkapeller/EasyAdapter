package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.elements.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
interface Binder {
    public CodeBlock prepare(CodeBlock codeBlock);
    public String resolveName(Variable paramModel);
}
