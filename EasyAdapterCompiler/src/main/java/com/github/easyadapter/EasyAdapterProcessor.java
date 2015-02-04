package com.github.easyadapter;

import com.github.easyadapter.builder.api.code.CodeBlock;
import com.github.easyadapter.builder.api.code.If;
import com.github.easyadapter.builder.api.elements.Type;
import com.github.easyadapter.builder.api.elements.Variable;
import com.github.easyadapter.builder.impl.AbsViewHolderBuilder;
import com.github.easyadapter.builder.impl.Types;
import com.github.easyadapter.builder.impl.VariableGenerator;
import com.github.easyadapter.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class EasyAdapterProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {

            final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {

                final TypeElement typeElement = (TypeElement) element;

                try {
                    final AbsViewHolderBuilder builder = new AbsViewHolderBuilder(processingEnv, typeElement) {
                        @Override
                        protected void analyzeModel(TypeElement type) {
                            List<? extends Element> elements = type.getEnclosedElements();
                            for (Element e : elements) {
                                if (Utils.isMethod(e)) {
                                    final ExecutableElement method = (ExecutableElement) e;

                                    final AnnotationValue onClickValue = getAnnotationValue(method, Annotations.ON_CLICK, "value");
                                    if (onClickValue != null) {
                                        final int viewId = (int) onClickValue.getValue();
                                        queueClickAction(viewId, new Action() {
                                            @Override
                                            public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                                                code.append(executeMethod(paramModel, method)).append(";\n");
                                            }
                                        });
                                    }

                                    final AnnotationValue onCheckedChangeValue = getAnnotationValue(method, Annotations.ON_CHECKED_CHANGED, "value");
                                    if (onCheckedChangeValue != null) {
                                        final int viewId = (int) onCheckedChangeValue.getValue();
                                        queueCheckedChangeAction(viewId, new Action() {
                                            @Override
                                            public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                                                code.append(executeMethod(paramModel, method)).append(";\n");
                                            }
                                        });
                                    }

                                    if (hasAnnotation(method, Annotations.BIND_TO_VIEW)) {

                                        final AnnotationValue viewIdValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "value");
                                        final AnnotationValue propertyValue = getAnnotationValue(method, Annotations.BIND_TO_VIEW, "property");

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

                                            queueBindAction(new Action() {
                                                @Override
                                                public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                                                    try {
                                                        switch (property) {

                                                            case AUTO:
                                                                throw new IllegalStateException("AUTO Property has not been handled correctly!");

                                                            case TEXT:
                                                                final String textValue = returnType.equals(Types.STRING) ? executeMethod(paramModel, method) : "String.valueOf(" + executeMethod(paramModel, method);
                                                                code.append(view(viewId, Types.Android.Views.TEXTVIEW)).append(".setText(").append(textValue).append(");\n");
                                                                break;

                                                            case TEXT_RESOURCE:
                                                                if (returnType.equals(Types.Primitives.INTEGER)) {
                                                                    code.append(view(viewId, Types.Android.Views.TEXTVIEW))
                                                                            .append(".setText(").append(executeMethod(paramModel, method)).append(");\n");
                                                                } else if (returnType.equals(Types.Boxed.INTEGER)) {
                                                                    Variable varTempResource = generator.generate(Integer.class);
                                                                    code.append(varTempResource.type()).append(" ").append(varTempResource.set(executeMethod(paramModel, method))).append(";\n");
                                                                    If checkNullIf = code.newIf(varTempResource + " != null");
                                                                    checkNullIf.whenTrue().append(view(viewId, Types.Android.Views.TEXTVIEW))
                                                                            .append(".setText(").append(varTempResource).append(");\n");
                                                                    checkNullIf.whenFalse().append(view(viewId, Types.Android.Views.TEXTVIEW))
                                                                            .append(".setText(\"\");\n");
                                                                } else {
                                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") is not a string resource id!", method);
                                                                }
                                                                break;

                                                            case BACKGROUND:
                                                                if (returnType.equals(Types.Primitives.INTEGER)) {
                                                                    code.append(view(viewId, Types.Android.Views.VIEW))
                                                                            .append(".setBackgroundResource(").append(executeMethod(paramModel, method)).append(");\n");
                                                                } else if (returnType.equals(Types.Boxed.INTEGER)) {
                                                                    Variable varTempResource = generator.generate(Integer.class);
                                                                    code.append(varTempResource.type()).append(" ").append(varTempResource.set(executeMethod(paramModel, method))).append(";\n");
                                                                    If checkNullIf = code.newIf(varTempResource + " != null");
                                                                    checkNullIf.whenTrue()
                                                                            .append(view(viewId, Types.Android.Views.VIEW)).append(".setBackgroundResource(").append(varTempResource).append(");\n");
                                                                    checkNullIf.whenFalse()
                                                                            .append(setBackgroundSafely().execute("this", view(viewId, Types.Android.Views.TEXTVIEW), "null")).append(";\n");
                                                                } else if (returnType.equals(Types.Android.DRAWABLE)) {
                                                                    code.append(setBackgroundSafely().execute("this", view(viewId, Types.Android.Views.TEXTVIEW), "null")).append(";\n");
                                                                } else {
                                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") is not a string resource id!", method);
                                                                }
                                                                break;

                                                            case IMAGE:
                                                                if (returnType.equals(Types.Primitives.INTEGER)) {
                                                                    code.append(view(viewId, Types.Android.Views.IMAGEVIEW))
                                                                            .append(".setImageResource(").append(executeMethod(paramModel, method)).append(");\n");
                                                                } else if (returnType.equals(Types.Boxed.INTEGER)) {
                                                                    Variable varTempResource = generator.generate(Integer.class);
                                                                    code.append(varTempResource.type()).append(" ").append(varTempResource.set(executeMethod(paramModel, method))).append(";\n");
                                                                    If checkNullIf = code.newIf(varTempResource + " != null");
                                                                    checkNullIf.whenTrue()
                                                                            .append(view(viewId, Types.Android.Views.IMAGEVIEW)).append(".setImageResource(").append(varTempResource).append(");\n");
                                                                    checkNullIf.whenFalse()
                                                                            .append(setBackgroundSafely().execute("this", view(viewId, Types.Android.Views.TEXTVIEW), "null")).append(";\n");
                                                                } else if (returnType.equals(Types.Android.DRAWABLE)) {
                                                                    code.append(view(viewId, Types.Android.Views.IMAGEVIEW))
                                                                            .append(".setImageDrawable(").append(executeMethod(paramModel, method)).append(");\n");
                                                                } else {
                                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") is not a string resource id!", method);
                                                                }
                                                                break;

                                                            case CHECKED_STATE:
                                                                if (returnType.equals(Types.Primitives.BOOLEAN)) {
                                                                    code.append(view(viewId, Types.Android.Views.CHECKBOX))
                                                                            .append(".setChecked(").append(executeMethod(paramModel, method)).append(");\n");
                                                                } else if (returnType.equals(Types.Boxed.BOOLEAN)) {
                                                                    Variable varTempCheckedState = generator.generate(Boolean.class);
                                                                    code.append(varTempCheckedState.type()).append(" ").append(varTempCheckedState.set(executeMethod(paramModel, method))).append(";\n");
                                                                    code.append(view(viewId, Types.Android.Views.CHECKBOX))
                                                                            .append(".setChecked(").append(varTempCheckedState).append(" != null ? ").append(varTempCheckedState).append(" : false").append(");\n");
                                                                } else {
                                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + method.getSimpleName() + " (" + returnType + ") cannot be applied to the checked state!", method);
                                                                }
                                                                break;

                                                            default:
                                                                break;
                                                        }
                                                    } catch (ViewTypeMismatchException e) {
                                                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), method);
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    if (hasAnnotation(method, Annotations.ON_BIND)) {
                                        queueBindAction(new Action() {
                                            @Override
                                            public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                                                code.append(executeMethod(paramModel, method)).append(";\n");
                                            }
                                        });
                                    }

                                    if (hasAnnotation(method, Annotations.ON_UNBIND)) {
                                        queueUnbindAction(new Action() {
                                            @Override
                                            public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                                                code.append(executeMethod(paramModel, method)).append(";\n");
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    };

                    builder.build();
                } catch (Exception e) {
                    Utils.error(processingEnv, "Could not generate view holder for an Element!", element);
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    enum BindProperty {
        AUTO,
        TEXT,
        TEXT_RESOURCE,
        BACKGROUND,
        IMAGE,
        CHECKED_STATE
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotations = new HashSet<String>();
        annotations.add(Annotations.LAYOUT);
        return annotations;
    }
}
