package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.If;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.processor.builder.exceptions.ViewTypeMismatchException;

import javax.tools.Diagnostic;

class TextResourceBinder {
    private final BindAction mBindAction;

    public TextResourceBinder(BindAction mBindAction) {
        this.mBindAction = mBindAction;
    }

    void bindTextResource(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        code = mBindAction.getResolver().wrapInIfWhenNecessary(mBindAction.getProcessingEnvironment(), code, mBindAction.getMethod());

        if (mBindAction.getValueWrapper().type.equals(Types.Primitives.INTEGER)) {
            handlePrimitiveTextResourceId(code);
        } else if (mBindAction.getValueWrapper().type.equals(Types.Boxed.INTEGER)) {
            handleBoxedTextResourceId(code, generator);
        } else {
            mBindAction.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + mBindAction.getMethod().getSimpleName() + " (" + mBindAction.getReturnType() + ") is not a string resource id!", mBindAction.getMethod());
        }
    }

    void handlePrimitiveTextResourceId(CodeBlock code) throws ViewTypeMismatchException {
        code.append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.TEXTVIEW))
                .append(".setText(").append(mBindAction.getValueWrapper().value).append(");\n");
    }

    void handleBoxedTextResourceId(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        Variable varTempResource = generator.generate(Integer.class);
        code.append(varTempResource.type()).append(" ").append(varTempResource.set(mBindAction.getValueWrapper().value)).append(";\n");
        If checkNullIf = code.newIf(varTempResource + " != null");
        checkNullIf.whenTrue().append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.TEXTVIEW))
                .append(".setText(").append(varTempResource).append(");\n");
        checkNullIf.whenFalse().append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.TEXTVIEW))
                .append(".setText(\"\");\n");
    }
}