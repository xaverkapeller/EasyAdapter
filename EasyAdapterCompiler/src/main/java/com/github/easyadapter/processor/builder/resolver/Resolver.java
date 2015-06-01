package com.github.easyadapter.processor.builder.resolver;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.codewriter.utils.Utils;
import com.github.easyadapter.processor.Annotations;
import com.github.easyadapter.processor.builder.exceptions.ViewTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
public class Resolver {

    private static final String FIELD_NAME_CURRENT_MODEL = "itemModel";

    private final VariableGenerator mFieldNameGenerator = new VariableGenerator();

    private final Map<Type, String> mInjectedTypeFieldNameMap = new HashMap<>();
    private final Map<Type, String> mFormaterFieldNameMap = new HashMap<>();
    private final Map<Type, String> mFormaterFieldInitializerMap = new HashMap<>();

    private final Map<Integer, Type> mViewTypeMap = new HashMap<>();
    private final Map<Integer, String> mViewFieldNameMap = new HashMap<>();

    private final ProcessingEnvironment mProcessingEnvironment;
    private final Type mModelType;
    private final String mViewHolderName;

    public Resolver(ProcessingEnvironment processingEnvironment, Type modelType, String viewHolderName) {
        mProcessingEnvironment = processingEnvironment;
        mModelType = modelType;
        mViewHolderName = viewHolderName;
        mFieldNameGenerator.setPrefix("_");
    }

    public CodeBlock wrapInIfWhenNecessary(ProcessingEnvironment processingEnvironment, CodeBlock code, ExecutableElement method) {
        if (Utils.hasAnnotation(method, Annotations.CALL_IF_SATISFIED)) {

            final StringBuilder comparisonBuilder = new StringBuilder();
            final List<? extends VariableElement> parameters = method.getParameters();
            for (int i = 0, count = parameters.size(); i < count; i++) {
                VariableElement var = parameters.get(i);
                final Type type = Types.create(var.asType());
                String parameterName;
                if (Utils.hasAnnotation(var, Annotations.INJECT)) {
                    parameterName = requestInjectedObject(type);
                } else if (Utils.hasAnnotation(var, Annotations.INJECT_VIEW)) {
                    final int viewId = (int) Utils.getAnnotationValue(var, Annotations.INJECT_VIEW, "value").getValue();
                    try {
                        parameterName = view(viewId, Types.Android.Views.VIEW);
                    } catch (ViewTypeMismatchException e) {
                        parameterName = null;
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), method);
                    }
                } else {
                    parameterName = null;
                    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not satisfy all the parameters of " + method.getSimpleName(), method);
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

    public String requestInjectedObject(Type type) {
        if (type.isSubTypeOf(mModelType)) {
            return FIELD_NAME_CURRENT_MODEL;
        }

        if (type.isSubTypeOf(Types.Android.RECYCLERVIEW_VIEWHOLDER)) {
            return mViewHolderName + ".this";
        }

        final Set<Type> types = mInjectedTypeFieldNameMap.keySet();
        for (Type injectedType : types) {
            if (type.isSubTypeOf(injectedType)) {
                return mInjectedTypeFieldNameMap.get(injectedType);
            }

            if (injectedType.isSubTypeOf(type)) {
                final String name = mInjectedTypeFieldNameMap.get(type);
                mInjectedTypeFieldNameMap.remove(injectedType);
                mInjectedTypeFieldNameMap.put(type, name);
                return name;
            }
        }

        final String name = mFieldNameGenerator.generateName();
        mInjectedTypeFieldNameMap.put(type, name);
        return name;
    }

    public String view(int id, Type type) throws ViewTypeMismatchException {

        if (mViewFieldNameMap.containsKey(id)) {
            final Type registeredType = mViewTypeMap.get(id);

            if (!registeredType.isSubTypeOf(type)) {
                if (type.isSubTypeOf(registeredType)) {
                    mViewTypeMap.put(id, type);
                } else {
                    throw new ViewTypeMismatchException("A View is being assumed to have conflicting types!", registeredType, type);
                }
            }

            return mViewFieldNameMap.get(id);
        } else {
            final String name = mFieldNameGenerator.generateName();
            mViewFieldNameMap.put(id, name);
            mViewTypeMap.put(id, type);

            return name;
        }
    }

    public String formater(Type formaterType) {
        if (mFormaterFieldNameMap.containsKey(formaterType)) {
            return mFormaterFieldNameMap.get(formaterType);
        }

        final TypeElement typeElement = formaterType.getTypeElement();
        final List<ExecutableElement> constructors = Utils.getConstructors(typeElement);

        if (constructors.size() > 1) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The Formater " + formaterType + " has more than one constructor!!1 Each Formater may only define one constructor!");
        } else if (constructors.size() < 1) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The Formater " + formaterType + " has more than no constructor!!1 Each Formater has to have exactly one constructor!");
        }

        final ExecutableElement constructorMethod = constructors.get(0);
        mFormaterFieldInitializerMap.put(formaterType, "new " + executeMethod(null, constructorMethod));

        final String fieldName = mFieldNameGenerator.generateName();
        mFormaterFieldNameMap.put(formaterType, fieldName);
        return fieldName;
    }

    public String executeMethod(String target, ExecutableElement method) {
        final StringBuilder builder = new StringBuilder();

        if (target != null) {
            builder.append(target).append(".");
        }

        if (method.getKind() == ElementKind.CONSTRUCTOR) {
            builder.append(method.getEnclosingElement().getSimpleName());
        } else {
            builder.append(method.getSimpleName());
        }

        builder.append("(");

        final List<? extends VariableElement> parameters = method.getParameters();
        for (int i = 0, parametersSize = parameters.size(); i < parametersSize; i++) {
            final VariableElement parameter = parameters.get(i);
            final Type parameterType = Types.create(parameter.asType());

            if (i > 0) {
                builder.append(", ");
            }

            final AnnotationValue viewIdValue = Utils.getAnnotationValue(parameter, Annotations.INJECT_VIEW, "value");
            if (viewIdValue != null) {
                final int viewId = (int) viewIdValue.getValue();
                try {
                    builder.append(view(viewId, parameterType));
                } catch (ViewTypeMismatchException e) {
                    mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), method);
                }
            } else if (Utils.hasAnnotation(parameter, Annotations.INJECT)) {
                builder.append(requestInjectedObject(parameterType));
            } else {
                mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not satisfy all the parameters of " + method.getSimpleName(), method);
            }
        }

        builder.append(")");

        return builder.toString();
    }

    public Map<Type, String> getInjectedTypeFieldNameMap() {
        return mInjectedTypeFieldNameMap;
    }

    public Map<Type, String> getFormaterFieldNameMap() {
        return mFormaterFieldNameMap;
    }

    public Map<Type, String> getFormaterFieldInitializerMap() {
        return mFormaterFieldInitializerMap;
    }

    public Map<Integer, Type> getViewTypeMap() {
        return mViewTypeMap;
    }

    public Map<Integer, String> getViewFieldNameMap() {
        return mViewFieldNameMap;
    }
}
