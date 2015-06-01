package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.If;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.processor.builder.exceptions.ViewTypeMismatchException;

import javax.tools.Diagnostic;

class BackgroundBinder {
    private final BindAction mBindAction;

    public BackgroundBinder(BindAction mBindAction) {
        this.mBindAction = mBindAction;
    }

    public void bindBackground(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        code = mBindAction.getResolver().wrapInIfWhenNecessary(mBindAction.getProcessingEnvironment(), code, mBindAction.getMethod());

        if (mBindAction.getValueWrapper().type.equals(Types.Primitives.INTEGER)) {
            handlePrimitiveDrawableResourceId(code);
        } else if (mBindAction.getValueWrapper().type.equals(Types.Boxed.INTEGER)) {
            handleBoxedDrawableResourceId(code, generator);
        } else if (mBindAction.getValueWrapper().type.equals(Types.Android.DRAWABLE)) {
            handleDrawableBackground(code);
        } else {
            mBindAction.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + mBindAction.getMethod().getSimpleName() + " (" + mBindAction.getReturnType() + ") is not a string resource id!", mBindAction.getMethod());
        }
    }

    public void handlePrimitiveDrawableResourceId(CodeBlock code) throws ViewTypeMismatchException {
        code.append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.VIEW))
                .append(".setBackgroundResource(").append(mBindAction.getValueWrapper().value).append(");\n");
    }

    public void handleBoxedDrawableResourceId(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        Variable varTempResource = generator.generate(Integer.class);
        code.append(varTempResource.type()).append(" ").append(varTempResource.set(mBindAction.getValueWrapper().value)).append(";\n");
        If checkNullIf = code.newIf(varTempResource + " != null");
        checkNullIf.whenTrue().append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.VIEW))
                .append(".setBackgroundResource(").append(varTempResource).append(");\n");
        appendSetDrawable(checkNullIf.whenFalse(), "null");
    }

    public void handleDrawableBackground(CodeBlock code) throws ViewTypeMismatchException {
        final String drawableParameter = mBindAction.getValueWrapper().value;
        appendSetDrawable(code, drawableParameter);
    }

    private void appendSetDrawable(CodeBlock code, String drawable) throws ViewTypeMismatchException {
        final String viewName = mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.VIEW);
        final If apiLevelIf = code.newIf("android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN");
        apiLevelIf.whenTrue().append(viewName).append(".setBackground(").append(drawable).append(");\n");
        apiLevelIf.whenFalse().append(viewName).append(".setBackgroundDrawable(").append(drawable).append(");\n");
    }
}