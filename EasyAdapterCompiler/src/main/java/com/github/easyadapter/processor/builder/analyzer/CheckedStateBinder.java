package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.processor.builder.exceptions.ViewTypeMismatchException;

import javax.tools.Diagnostic;

class CheckedStateBinder {
    private final BindAction mBindAction;

    public CheckedStateBinder(BindAction mBindAction) {
        this.mBindAction = mBindAction;
    }

    void bindCheckedState(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        code = mBindAction.getResolver().wrapInIfWhenNecessary(mBindAction.getProcessingEnvironment(), code, mBindAction.getMethod());

        if (mBindAction.getValueWrapper().type.equals(Types.Primitives.BOOLEAN)) {
            handlePrimitiveBooleanCheckedState(code);
        } else if (mBindAction.getValueWrapper().type.equals(Types.Boxed.BOOLEAN)) {
            handleBoxedBooleanCheckedState(code, generator);
        } else {
            mBindAction.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + mBindAction.getMethod().getSimpleName() + " (" + mBindAction.getReturnType() + ") cannot be applied to the checked state!", mBindAction.getMethod());
        }
    }

    void handlePrimitiveBooleanCheckedState(CodeBlock code) throws ViewTypeMismatchException {
        code.append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.CHECKBOX))
                .append(".setChecked(").append(mBindAction.getValueWrapper().value).append(");\n");
    }

    void handleBoxedBooleanCheckedState(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        Variable varTempCheckedState = generator.generate(Boolean.class);
        code.append(varTempCheckedState.type()).append(" ").append(varTempCheckedState.set(mBindAction.getValueWrapper().value)).append(";\n");
        code.append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.CHECKBOX))
                .append(".setChecked(").append(varTempCheckedState).append(" != null ? ").append(varTempCheckedState).append(" : false").append(");\n");
    }
}