package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.codewriter.utils.Utils;
import com.github.easyadapter.processor.Annotations;
import com.github.easyadapter.processor.DefinedTypes;
import com.github.easyadapter.processor.builder.resolver.Resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
public class ViewModelAnalyzer {

    private static final String VIEW_HOLDER_POSTFIX = "$$ViewHolder";
    private static final String VIEW_HOLDER_FACTORY_POSTFIX = "$$ViewHolderFactory";

    private final List<Action> mBindActions = new ArrayList<>();
    private final List<Action> mUnbindActions = new ArrayList<>();
    private final Map<Integer, List<Action>> mClickActions = new HashMap<>();
    private final Map<Integer, List<Action>> mCheckedChangeActions = new HashMap<>();

    private final ProcessingEnvironment mProcessingEnvironment;

    private Type mModelType;
    private String mViewHolderName;
    private String mViewHolderFactoryName;
    private Resolver mResolver;

    public ViewModelAnalyzer(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public AnalyzerResult analyze(TypeElement element, List<TypeElement> listeners) {
        resetQueues();
        performSetup(element);

        if (!implementsViewModel(element)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, element.getQualifiedName() + " does not implement the ViewModel interface! All view models have to implement it.", element);
        }

        handleAnnotatedViewModelMethods(element);
        handleAnnotatedListenerMethods(listeners);

        return new AnalyzerResultImpl(
                getLayoutId(element),
                mModelType,
                element,
                mViewHolderName,
                mViewHolderFactoryName,
                new ArrayList<>(mBindActions),
                new ArrayList<>(mUnbindActions),
                new HashMap<>(mCheckedChangeActions),
                new HashMap<>(mClickActions),
                mResolver);
    }

    private void resetQueues() {
        mBindActions.clear();
        mUnbindActions.clear();
        mClickActions.clear();
        mCheckedChangeActions.clear();
    }

    private void performSetup(TypeElement element) {
        final String modelName = element.getSimpleName().toString();
        mViewHolderName = modelName + VIEW_HOLDER_POSTFIX;
        mViewHolderFactoryName = modelName + VIEW_HOLDER_FACTORY_POSTFIX;
        mModelType = Types.create(element);
        mResolver = new Resolver(mProcessingEnvironment, mModelType, mViewHolderName);
    }

    private void handleAnnotatedViewModelMethods(TypeElement type) {
        final List<? extends Element> enclosedElements = type.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }

            final ExecutableElement method = (ExecutableElement) element;
            handleViewModelMethod(method, new Binder() {
                @Override
                public CodeBlock prepare(CodeBlock codeBlock) {
                    return codeBlock;
                }

                @Override
                public String resolveName(Variable paramModel) {
                    return paramModel.name();
                }
            });
        }
    }

    private void handleAnnotatedListenerMethods(List<TypeElement> listeners) {
        for (TypeElement listener : listeners) {
            final List<? extends Element> enclosedElements = listener.getEnclosedElements();
            for (Element element : enclosedElements) {
                if (element.getKind() != ElementKind.METHOD) {
                    continue;
                }

                final ExecutableElement method = (ExecutableElement) element;
                final Type listenerType = Types.create(listener);
                final String fieldName = mResolver.requestInjectedObject(listenerType);
                handleViewModelMethod(method, new Binder() {
                    @Override
                    public CodeBlock prepare(CodeBlock codeBlock) {
                        return codeBlock.newIf(fieldName + " != null").whenTrue();
                    }

                    @Override
                    public String resolveName(Variable paramModel) {
                        return fieldName;
                    }
                });
            }
        }
    }

    private void queueBindAction(Action action) {
        mBindActions.add(action);
    }

    private void queueUnbindAction(Action action) {
        mUnbindActions.add(action);
    }

    private void queueClickAction(int id, Action action) {
        if (!mClickActions.containsKey(id)) {
            mClickActions.put(id, new ArrayList<Action>());
        }

        mClickActions.get(id).add(action);
    }

    private void queueCheckedChangeAction(int id, Action action) {
        if (!mCheckedChangeActions.containsKey(id)) {
            mCheckedChangeActions.put(id, new ArrayList<Action>());
        }

        mCheckedChangeActions.get(id).add(action);
    }

    private void handleViewModelMethod(final ExecutableElement method, final Binder binder) {
        final AnnotationValue onClickValue = Utils.getAnnotationValue(method, Annotations.ON_CLICK, "value");
        if (onClickValue != null) {
            final int viewId = (int) onClickValue.getValue();
            queueClickAction(viewId, new Action() {
                @Override
                public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                    code = binder.prepare(code);
                    code = mResolver.wrapInIfWhenNecessary(mProcessingEnvironment, code, method);
                    code.append(mResolver.executeMethod(binder.resolveName(paramModel), method)).append(";\n");
                }
            });
        }

        final AnnotationValue onCheckedChangeValue = Utils.getAnnotationValue(method, Annotations.ON_CHECKED_CHANGED, "value");
        if (onCheckedChangeValue != null) {
            final int viewId = (int) onCheckedChangeValue.getValue();
            queueCheckedChangeAction(viewId, new Action() {
                @Override
                public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                    code = binder.prepare(code);
                    code = mResolver.wrapInIfWhenNecessary(mProcessingEnvironment, code, method);
                    code.append(mResolver.executeMethod(binder.resolveName(paramModel), method)).append(";\n");
                }
            });
        }

        if (Utils.hasAnnotation(method, Annotations.BIND_TO_VIEW)) {
            handleBindToView(method, binder);
        }

        if (Utils.hasAnnotation(method, Annotations.ON_BIND)) {
            queueBindAction(new Action() {
                @Override
                public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                    code = binder.prepare(code);
                    code = mResolver.wrapInIfWhenNecessary(mProcessingEnvironment, code, method);
                    code.append(mResolver.executeMethod(binder.resolveName(paramModel), method)).append(";\n");
                }
            });
        }

        if (Utils.hasAnnotation(method, Annotations.ON_UNBIND)) {
            queueUnbindAction(new Action() {
                @Override
                public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator) {
                    code = binder.prepare(code);
                    code = mResolver.wrapInIfWhenNecessary(mProcessingEnvironment, code, method);
                    code.append(mResolver.executeMethod(binder.resolveName(paramModel), method)).append(";\n");
                }
            });
        }
    }

    private void handleBindToView(ExecutableElement method, Binder binder) {
        final AnnotationValue viewIdValue = Utils.getAnnotationValue(method, Annotations.BIND_TO_VIEW, "value");
        final AnnotationValue propertyValue = Utils.getAnnotationValue(method, Annotations.BIND_TO_VIEW, "property");
        final AnnotationValue formaterValue = Utils.getAnnotationValue(method, Annotations.BIND_TO_VIEW, "formater");

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
        final BindProperty property = resolveBindProperty(propertyValue, returnType);

        for (AnnotationValue value : viewIds) {
            final int viewId = (int) value.getValue();
            queueBindAction(new BindAction(mProcessingEnvironment, binder, property, method, returnType, formaterType, formaterInputType, formaterOutputType, viewId, mResolver));
        }
    }

    private BindProperty resolveBindProperty(AnnotationValue propertyValue, Type returnType) {
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
        return property;
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
            if (DefinedTypes.FORMATER.equals(type)) {
                return declaredType;
            }
        }

        final TypeElement superElement = Utils.getSuperElement(implementedType);
        if (superElement != null) {
            return findFormaterType(superElement);
        }

        return null;
    }

    private boolean implementsViewModel(TypeElement element) {
        for (TypeMirror mirror : element.getInterfaces()) {
            if (mirror.toString().equals(DefinedTypes.VIEW_MODEL.fullClassName())) {
                return true;
            }
        }
        return false;
    }

    private int getLayoutId(TypeElement model) {
        for (AnnotationMirror mirror : model.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(Annotations.LAYOUT)) {
                return (int) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
            }
        }
        throw new IllegalStateException("@Layout Annotation is missing!");
    }
}
