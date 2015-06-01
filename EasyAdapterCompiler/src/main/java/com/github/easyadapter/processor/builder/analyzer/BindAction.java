package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.processor.builder.resolver.Resolver;
import com.github.easyadapter.processor.builder.exceptions.ViewTypeMismatchException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
class BindAction implements Action {

    private final ProcessingEnvironment mProcessingEnvironment;
    private final BindProperty mProperty;
    private final ExecutableElement mMethod;
    private final Type mReturnType;
    private final Type mFormaterType;
    private final Type mFormaterInputType;
    private final Type mFormaterOutputType;
    private final int mViewId;
    private final Binder mBinder;

    private final Resolver mResolver;
    private ValueWrapper mValueWrapper;

    private final TextResourceBinder mTextResourceBinder = new TextResourceBinder(this);
    private final BackgroundBinder mBackgroundBinder = new BackgroundBinder(this);
    private final ImageBinder mImageBinder = new ImageBinder(this);
    private final CheckedStateBinder mCheckedStateBinder = new CheckedStateBinder(this);

    public BindAction(ProcessingEnvironment processingEnvironment, Binder binder, BindProperty property, ExecutableElement method, Type returnType, Type formaterType, Type formaterInputType, Type formaterOutputType, int viewId, Resolver resolver) {
        mProcessingEnvironment = processingEnvironment;
        mBinder = binder;
        mProperty = property;
        mMethod = method;
        mReturnType = returnType;
        mFormaterType = formaterType;
        mFormaterInputType = formaterInputType;
        mFormaterOutputType = formaterOutputType;
        mViewId = viewId;
        mResolver = resolver;
    }

    @Override
    public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
        code = mBinder.prepare(code);
        mValueWrapper = evaluateValue(mFormaterType, mFormaterInputType, mFormaterOutputType, mReturnType, mBinder.resolveName(paramModel), mMethod);
        try {
            switch (mProperty) {

                case AUTO:
                    throw new IllegalStateException("AUTO Property has not been handled correctly!");

                case TEXT:
                    bindText(code);
                    break;

                case TEXT_RESOURCE:
                    mTextResourceBinder.bindTextResource(code, generator);
                    break;

                case BACKGROUND:
                    mBackgroundBinder.bindBackground(code, generator);
                    break;

                case IMAGE:
                    mImageBinder.bindImage(code, generator);
                    break;

                case CHECKED_STATE:
                    mCheckedStateBinder.bindCheckedState(code, generator);
                    break;

                default:
                    break;
            }
        } catch (ViewTypeMismatchException e) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), mMethod);
        }
    }

    private ValueWrapper evaluateValue(Type formaterType, Type formaterInputType, Type formaterOutputType, Type returnType, String targetName, ExecutableElement method) {
        final String value = mResolver.executeMethod(targetName, method);
        if (formaterType != null) {

            if (!returnType.equals(formaterInputType)) {
                mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        formaterType.fullClassName() + " cannot be applied to "
                                + method.getSimpleName() + "! The Input Type for the Formater ("
                                + formaterInputType.fullClassName() + ") does not match the return type of the method!!1",
                        method);
            }

            return new ValueWrapper(mResolver.formater(formaterType) + ".format(" + value + ")", formaterOutputType);
        }
        return new ValueWrapper(value, returnType);
    }

    private void bindText(CodeBlock code) throws ViewTypeMismatchException {
        code = mResolver.wrapInIfWhenNecessary(mProcessingEnvironment, code, mMethod);
        final String textValue = mValueWrapper.type.equals(Types.STRING) ? mValueWrapper.value : "String.valueOf(" + mValueWrapper.value + ")";
        code.append(mResolver.view(mViewId, Types.Android.Views.TEXTVIEW)).append(".setText(").append(textValue).append(");\n");
    }

    public int getViewId() {
        return mViewId;
    }

    public Resolver getResolver() {
        return mResolver;
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return mProcessingEnvironment;
    }

    public Type getReturnType() {
        return mReturnType;
    }

    public ValueWrapper getValueWrapper() {
        return mValueWrapper;
    }

    public ExecutableElement getMethod() {
        return mMethod;
    }
}
