package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.If;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.processor.builder.exceptions.ViewTypeMismatchException;

import javax.tools.Diagnostic;

class ImageBinder {
    private final BindAction mBindAction;

    public ImageBinder(BindAction mBindAction) {
        this.mBindAction = mBindAction;
    }

    void bindImage(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        code = mBindAction.getResolver().wrapInIfWhenNecessary(mBindAction.getProcessingEnvironment(), code, mBindAction.getMethod());

        if (mBindAction.getValueWrapper().type.equals(Types.Primitives.INTEGER)) {
            handlePrimitiveImageResourceId(code);
        } else if (mBindAction.getValueWrapper().type.equals(Types.Boxed.INTEGER)) {
            handleBoxedImageResourceId(code, generator);
        } else if (mBindAction.getValueWrapper().type.equals(Types.Android.DRAWABLE)) {
            handleDrawableImage(code);
        } else {
            mBindAction.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + mBindAction.getMethod().getSimpleName() + " (" + mBindAction.getReturnType() + ") is not a string resource id!", mBindAction.getMethod());
        }
    }

    void handlePrimitiveImageResourceId(CodeBlock code) throws ViewTypeMismatchException {
        code.append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.IMAGEVIEW))
                .append(".setImageResource(").append(mBindAction.getValueWrapper().value).append(");\n");
    }

    void handleBoxedImageResourceId(CodeBlock code, VariableGenerator generator) throws ViewTypeMismatchException {
        Variable varTempResource = generator.generate(Integer.class);
        code.append(varTempResource.type()).append(" ").append(varTempResource.set(mBindAction.getValueWrapper().value)).append(";\n");
        If checkNullIf = code.newIf(varTempResource + " != null");
        checkNullIf.whenTrue()
                .append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.IMAGEVIEW))
                .append(".setImageResource(").append(varTempResource).append(");\n");
        appendSetDrawable(checkNullIf.whenFalse(), "null");
    }

    void handleDrawableImage(CodeBlock code) throws ViewTypeMismatchException {
        code.append(mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.IMAGEVIEW))
                .append(".setImageDrawable(").append(mBindAction.getValueWrapper().value).append(");\n");
    }

    private void appendSetDrawable(CodeBlock code, String drawable) throws ViewTypeMismatchException {
        final String viewName = mBindAction.getResolver().view(mBindAction.getViewId(), Types.Android.Views.TEXTVIEW);
        final If apiLevelIf = code.newIf("android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN");
        apiLevelIf.whenTrue().append(viewName).append(".setBackground(").append(drawable).append(");\n");
        apiLevelIf.whenFalse().append(viewName).append(".setBackgroundDrawable(").append(drawable).append(");\n");
    }
}