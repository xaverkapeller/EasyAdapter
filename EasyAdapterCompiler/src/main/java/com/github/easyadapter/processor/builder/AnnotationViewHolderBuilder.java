package com.github.easyadapter.processor.builder;

import com.github.easyadapter.processor.Annotations;
import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.If;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.codewriter.utils.Utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by kapeller on 09/04/15.
 */
public class AnnotationViewHolderBuilder extends AbsViewHolderBuilder {

    public static final Type FORMATER = Types.create("com.github.easyadapter.api", "Formater", EnumSet.of(Modifier.PUBLIC), null);

    public enum BindProperty {
        AUTO,
        TEXT,
        TEXT_RESOURCE,
        BACKGROUND,
        IMAGE,
        CHECKED_STATE
    }

    private final List<TypeElement> listeners;

    public AnnotationViewHolderBuilder(ProcessingEnvironment environment, TypeElement element, List<TypeElement> listeners) {
        super(environment, element);

        this.listeners = listeners;
    }

    @Override
    protected void analyzeModel(TypeElement type) {

        final List<TypeElement> filteredListeners = listeners.stream()
                .filter(listener -> getAnnotationValue(listener, Annotations.LISTENER, "value").getValue().toString().equals(type.asType().toString()))
                .collect(Collectors.toList());

        type.getEnclosedElements().stream()
                .filter(Utils::isMethod)
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .forEach(this::handleViewModelMethod);

        filteredListeners.stream().forEach((listener) -> listener.getEnclosedElements().stream()
                .filter(Utils::isMethod)
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .forEach(method -> {
                    final Type listenerType = Types.create(listener);
                    final String fieldName = requestInjectedObject(listenerType);
                    handleListenerMethod(fieldName, method);
                }));
    }

    private void handleViewModelMethod(ExecutableElement method) {
        final AnnotationValue onClickValue = getAnnotationValue(method, Annotations.ON_CLICK, "value");
        if (onClickValue != null) {
            final int viewId = (int) onClickValue.getValue();
            queueClickAction(viewId, (code, paramModel, generator) -> {
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(paramModel, method)).append(";\n");
            });
        }

        final AnnotationValue onCheckedChangeValue = getAnnotationValue(method, Annotations.ON_CHECKED_CHANGED, "value");
        if (onCheckedChangeValue != null) {
            final int viewId = (int) onCheckedChangeValue.getValue();
            queueCheckedChangeAction(viewId, (code, paramModel, generator) -> {
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(paramModel, method)).append(";\n");
            });
        }

        if (hasAnnotation(method, Annotations.BIND_TO_VIEW)) {

            final AnnotationValue viewIdValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "value");
            final AnnotationValue propertyValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "property");
            final AnnotationValue formaterValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "formater");

            final Type formaterType;
            final Type formaterInputType;
            final Type formaterOutputType;
            if (formaterValue != null) {
                final TypeMirror mirror = (TypeMirror) formaterValue.getValue();
                final List<Type> typeParameters = getFormaterTypeParameters(mirror);
                formaterType = Types.create(mirror);
                formaterInputType = typeParameters.get(0);
                formaterOutputType = typeParameters.get(1);
            } else {
                formaterType = null;
                formaterInputType = null;
                formaterOutputType = null;
            }

            final List<AnnotationValue> viewIds = (List<AnnotationValue>) viewIdValue.getValue();
            final Type returnType = Types.create(method.getReturnType());

            final BindProperty tempProperty = propertyValue != null ? BindProperty.valueOf(propertyValue.getValue().toString()) : BindProperty.AUTO;
            final BindProperty property;
            if (tempProperty == BindProperty.AUTO) {
                if (returnType.equals(Types.Primitives.INTEGER) || returnType.equals(Types.Boxed.INTEGER)) {
                    property = BindProperty.TEXT_RESOURCE;
                } else if (returnType.equals(Types.Primitives.BOOLEAN) || returnType.equals(Types.Boxed.BOOLEAN)) {
                    property = BindProperty.CHECKED_STATE;
                } else if (returnType.equals(Types.Android.DRAWABLE)) {
                    property = BindProperty.IMAGE;
                } else {
                    property = BindProperty.TEXT;
                }
            } else {
                property = tempProperty;
            }

            for (AnnotationValue value : viewIds) {
                final int viewId = (int) value.getValue();

                queueBindAction((code, paramModel, generator) -> buildBindAction(code, paramModel.name(), generator, property, method, returnType, viewId, formaterType, formaterInputType, formaterOutputType));
            }
        }

        if (hasAnnotation(method, Annotations.ON_BIND)) {
            queueBindAction((code, paramModel, generator) -> {
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(paramModel, method)).append(";\n");
            });
        }

        if (hasAnnotation(method, Annotations.ON_UNBIND)) {
            queueUnbindAction((code, paramModel, generator) -> {
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(paramModel, method)).append(";\n");
            });
        }
    }

    private void handleListenerMethod(String listenerFieldName, ExecutableElement method) {
        final AnnotationValue onClickValue = getAnnotationValue(method, Annotations.ON_CLICK, "value");
        if (onClickValue != null) {
            final int viewId = (int) onClickValue.getValue();
            queueClickAction(viewId, (code, paramModel, generator) -> {
                code = code.newIf(listenerFieldName + " != null").whenTrue();
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(listenerFieldName, method)).append(";\n");
            });
        }

        final AnnotationValue onCheckedChangeValue = getAnnotationValue(method, Annotations.ON_CHECKED_CHANGED, "value");
        if (onCheckedChangeValue != null) {
            final int viewId = (int) onCheckedChangeValue.getValue();
            queueCheckedChangeAction(viewId, (code, paramModel, generator) -> {
                code = code.newIf(listenerFieldName + " != null").whenTrue();
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(listenerFieldName, method)).append(";\n");
            });
        }

        if (hasAnnotation(method, Annotations.BIND_TO_VIEW)) {

            final AnnotationValue viewIdValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "value");
            final AnnotationValue propertyValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "property");
            final AnnotationValue formaterValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "formater");

            final Type formaterType;
            final Type formaterInputType;
            final Type formaterOutputType;
            if (formaterValue != null) {
                final TypeMirror mirror = (TypeMirror) formaterValue.getValue();
                final List<Type> typeParameters = getFormaterTypeParameters(mirror);
                formaterType = Types.create(mirror);
                formaterInputType = typeParameters.get(0);
                formaterOutputType = typeParameters.get(1);
            } else {
                formaterType = null;
                formaterInputType = null;
                formaterOutputType = null;
            }

            final List<AnnotationValue> viewIds = (List<AnnotationValue>) viewIdValue.getValue();
            final Type returnType = Types.create(method.getReturnType());

            final BindProperty tempProperty = propertyValue != null ? BindProperty.valueOf(propertyValue.getValue().toString()) : BindProperty.AUTO;
            final BindProperty property;
            if (tempProperty == BindProperty.AUTO) {
                if (returnType.equals(Types.Primitives.INTEGER) || returnType.equals(Types.Boxed.INTEGER)) {
                    property = BindProperty.TEXT_RESOURCE;
                } else if (returnType.equals(Types.Primitives.BOOLEAN) || returnType.equals(Types.Boxed.BOOLEAN)) {
                    property = BindProperty.CHECKED_STATE;
                } else if (returnType.equals(Types.Android.DRAWABLE)) {
                    property = BindProperty.IMAGE;
                } else {
                    property = BindProperty.TEXT;
                }
            } else {
                property = tempProperty;
            }

            for (AnnotationValue value : viewIds) {
                final int viewId = (int) value.getValue();

                queueBindAction((code, paramModel, generator) -> buildBindAction(code, listenerFieldName, generator, property, method, returnType, viewId, formaterType, formaterInputType, formaterOutputType));
            }
        }

        if (hasAnnotation(method, Annotations.ON_BIND)) {
            queueBindAction((code, paramModel, generator) -> {
                code = code.newIf(listenerFieldName + " != null").whenTrue();
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(listenerFieldName, method)).append(";\n");
            });
        }

        if (hasAnnotation(method, Annotations.ON_UNBIND)) {
            queueUnbindAction((code, paramModel, generator) -> {
                code = code.newIf(listenerFieldName + " != null").whenTrue();
                code = wrapInIfwhenNecessary(code, method);
                code.append(executeMethod(listenerFieldName, method)).append(";\n");
            });
        }
    }

    private void buildBindAction(CodeBlock code, String targetName, VariableGenerator generator, BindProperty property,
                                 ExecutableElement method, Type returnType, int viewId, Type formaterType,
                                 Type formaterInputType, Type formaterOutputType) {
        try {
            switch (property) {

                case AUTO:
                    throw new IllegalStateException("AUTO Property has not been handled correctly!");

                case TEXT:
                    bindText(code, targetName, generator, viewId, method, returnType, formaterType, formaterInputType, formaterOutputType);
                    break;

                case TEXT_RESOURCE:
                    bindTextResource(code, targetName, generator, viewId, method, returnType, formaterType, formaterInputType, formaterOutputType);
                    break;

                case BACKGROUND:
                    bindBackground(code, targetName, generator, viewId, method, returnType, formaterType, formaterInputType, formaterOutputType);
                    break;

                case IMAGE:
                    bindImage(code, targetName, generator, viewId, method, returnType, formaterType, formaterInputType, formaterOutputType);
                    break;

                case CHECKED_STATE:
                    bindCheckedState(code, targetName, generator, viewId, method, returnType, formaterType, formaterInputType, formaterOutputType);
                    break;

                default:
                    break;
            }
        } catch (ViewTypeMismatchException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), method);
        }
    }

    class ValueWrapper {
        private final String value;
        private final Type type;

        private ValueWrapper(String value, Type type) {
            this.value = value;
            this.type = type;
        }
    }

    private ValueWrapper evaluateValue(Type formaterType, Type formaterInputType, Type formaterOutputType, Type returnType, ExecutableElement method, String targetName) {
        final String value = executeMethod(targetName, method);
        if (formaterType != null) {

            if (!returnType.equals(formaterInputType)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        formaterType.fullClassName() + " cannot be applied to "
                                + method.getSimpleName() + "! The Input Type for the Formater ("
                                + formaterInputType.fullClassName() + ") does not match the return type of the method!!1",
                        method);
            }

            return new ValueWrapper(formater(formaterType) + ".format(" + value + ")", formaterOutputType);
        }
        return new ValueWrapper(value, returnType);
    }

    private CodeBlock wrapInIfwhenNecessary(CodeBlock code, ExecutableElement method) {
        if (hasAnnotation(method, Annotations.CALL_IF_SATISFIED)) {

            final StringBuilder comparisonBuilder = new StringBuilder();
            final List<? extends VariableElement> parameters = method.getParameters();
            for (int i = 0, count = parameters.size(); i < count; i++) {
                VariableElement var = parameters.get(i);
                final Type type = Types.create(var.asType());
                String parameterName;
                if (hasAnnotation(var, Annotations.INJECT)) {
                    parameterName = requestInjectedObject(type);
                } else if (hasAnnotation(var, Annotations.INJECT_VIEW)) {
                    final int viewId = (int) getAnnotationValue(var, Annotations.INJECT_VIEW, "value").getValue();
                    try {
                        parameterName = view(viewId, Types.Android.Views.VIEW);
                    } catch (ViewTypeMismatchException e) {
                        parameterName = null;
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), method);
                    }
                } else {
                    parameterName = null;
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not satisfy all the parameters of " + method.getSimpleName(), method);
                }

                if (i > 0) {
                    comparisonBuilder.append(" && ");
                }

                comparisonBuilder.append(parameterName).append(" != null");
            }

            return code.newIf(comparisonBuilder.toString()).whenTrue();
        }
        return code;
    }

    private void bindText(CodeBlock code, String targetName, VariableGenerator generator, int viewId, ExecutableElement method, Type returnType, Type formaterType, Type formaterInputType, Type formaterOutputType) throws ViewTypeMismatchException {
        final ValueWrapper valueWrapper = evaluateValue(formaterType, formaterInputType, formaterOutputType, returnType, method, targetName);

        code = wrapInIfwhenNecessary(code, method);

        final String textValue = valueWrapper.type.equals(Types.STRING) ? valueWrapper.value : "String.valueOf(" + valueWrapper.value + ")";
        code.append(view(viewId, Types.Android.Views.TEXTVIEW)).append(".setText(").append(textValue).append(");\n");
    }

    private void bindTextResource(CodeBlock code, String targetName, VariableGenerator generator, int viewId, ExecutableElement method, Type returnType, Type formaterType, Type formaterInputType, Type formaterOutputType) throws ViewTypeMismatchException {
        final ValueWrapper valueWrapper = evaluateValue(formaterType, formaterInputType, formaterOutputType, returnType, method, targetName);

        code = wrapInIfwhenNecessary(code, method);

        if (valueWrapper.type.equals(Types.Primitives.INTEGER)) {
            code.append(view(viewId, Types.Android.Views.TEXTVIEW))
                    .append(".setText(").append(valueWrapper.value).append(");\n");
        } else if (valueWrapper.type.equals(Types.Boxed.INTEGER)) {
            Variable varTempResource = generator.generate(Integer.class);
            code.append(varTempResource.type()).append(" ").append(varTempResource.set(valueWrapper.value)).append(";\n");
            If checkNullIf = code.newIf(varTempResource + " != null");
            checkNullIf.whenTrue().append(view(viewId, Types.Android.Views.TEXTVIEW))
                    .append(".setText(").append(varTempResource).append(");\n");
            checkNullIf.whenFalse().append(view(viewId, Types.Android.Views.TEXTVIEW))
                    .append(".setText(\"\");\n");
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") is not a string resource id!", method);
        }
    }

    private void bindBackground(CodeBlock code, String targetName, VariableGenerator generator, int viewId, ExecutableElement method, Type returnType, Type formaterType, Type formaterInputType, Type formaterOutputType) throws ViewTypeMismatchException {
        final ValueWrapper valueWrapper = evaluateValue(formaterType, formaterInputType, formaterOutputType, returnType, method, targetName);

        code = wrapInIfwhenNecessary(code, method);

        if (valueWrapper.type.equals(Types.Primitives.INTEGER)) {
            code.append(view(viewId, Types.Android.Views.VIEW))
                    .append(".setBackgroundResource(").append(valueWrapper.value).append(");\n");
        } else if (valueWrapper.type.equals(Types.Boxed.INTEGER)) {
            Variable varTempResource = generator.generate(Integer.class);
            code.append(varTempResource.type()).append(" ").append(varTempResource.set(valueWrapper.value)).append(";\n");
            If checkNullIf = code.newIf(varTempResource + " != null");
            checkNullIf.whenTrue()
                    .append(view(viewId, Types.Android.Views.VIEW)).append(".setBackgroundResource(").append(varTempResource).append(");\n");
            checkNullIf.whenFalse()
                    .append(setBackgroundSafely().execute("this", view(viewId, Types.Android.Views.VIEW), "null")).append(";\n");
        } else if (valueWrapper.type.equals(Types.Android.DRAWABLE)) {
            code.append(setBackgroundSafely().execute("this", view(viewId, Types.Android.Views.VIEW), valueWrapper.value)).append(";\n");
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") is not a string resource id!", method);
        }
    }

    private void bindImage(CodeBlock code, String targetName, VariableGenerator generator, int viewId, ExecutableElement method, Type returnType, Type formaterType, Type formaterInputType, Type formaterOutputType) throws ViewTypeMismatchException {
        final ValueWrapper valueWrapper = evaluateValue(formaterType, formaterInputType, formaterOutputType, returnType, method, targetName);

        code = wrapInIfwhenNecessary(code, method);

        if (valueWrapper.type.equals(Types.Primitives.INTEGER)) {
            code.append(view(viewId, Types.Android.Views.IMAGEVIEW))
                    .append(".setImageResource(").append(valueWrapper.value).append(");\n");
        } else if (valueWrapper.type.equals(Types.Boxed.INTEGER)) {
            Variable varTempResource = generator.generate(Integer.class);
            code.append(varTempResource.type()).append(" ").append(varTempResource.set(valueWrapper.value)).append(";\n");
            If checkNullIf = code.newIf(varTempResource + " != null");
            checkNullIf.whenTrue()
                    .append(view(viewId, Types.Android.Views.IMAGEVIEW)).append(".setImageResource(").append(varTempResource).append(");\n");
            checkNullIf.whenFalse()
                    .append(setBackgroundSafely().execute("this", view(viewId, Types.Android.Views.TEXTVIEW), "null")).append(";\n");
        } else if (valueWrapper.type.equals(Types.Android.DRAWABLE)) {
            code.append(view(viewId, Types.Android.Views.IMAGEVIEW))
                    .append(".setImageDrawable(").append(valueWrapper.value).append(");\n");
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") is not a string resource id!", method);
        }
    }

    private void bindCheckedState(CodeBlock code, String targetName, VariableGenerator generator, int viewId, ExecutableElement method, Type returnType, Type formaterType, Type formaterInputType, Type formaterOutputType) throws ViewTypeMismatchException {
        final ValueWrapper valueWrapper = evaluateValue(formaterType, formaterInputType, formaterOutputType, returnType, method, targetName);

        code = wrapInIfwhenNecessary(code, method);

        if (valueWrapper.type.equals(Types.Primitives.BOOLEAN)) {
            code.append(view(viewId, Types.Android.Views.CHECKBOX))
                    .append(".setChecked(").append(valueWrapper.value).append(");\n");
        } else if (valueWrapper.type.equals(Types.Boxed.BOOLEAN)) {
            Variable varTempCheckedState = generator.generate(Boolean.class);
            code.append(varTempCheckedState.type()).append(" ").append(varTempCheckedState.set(valueWrapper.value)).append(";\n");
            code.append(view(viewId, Types.Android.Views.CHECKBOX))
                    .append(".setChecked(").append(varTempCheckedState).append(" != null ? ").append(varTempCheckedState).append(" : false").append(");\n");
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") cannot be applied to the checked state!", method);
        }
    }

    private List<Type> getFormaterTypeParameters(TypeMirror mirror) {
        final List<Type> parameters = new ArrayList<>();

        final DeclaredType declaredType = (DeclaredType) mirror;
        final TypeElement implementedType = (TypeElement) declaredType.asElement();
        final DeclaredType formaterType = findFormaterType(implementedType);
        if (formaterType != null) {
            for (TypeMirror a : formaterType.getTypeArguments()) {
                parameters.add(Types.create(a));
            }
        }

        return parameters;
    }

    private DeclaredType findFormaterType(TypeElement implementedType) {
        for (TypeMirror interfaceType : implementedType.getInterfaces()) {
            final DeclaredType declaredType = (DeclaredType) interfaceType;

            final Type type = Types.create(declaredType);
            if (FORMATER.equals(type)) {
                return declaredType;
            }
        }

        final TypeElement superElement = Utils.getSuperElement(implementedType);
        if (superElement != null) {
            return findFormaterType(superElement);
        }

        return null;
    }
}
