package com.github.easyadapter.builder;

import com.github.easyadapter.Annotations;
import com.github.wrdlbrnft.codebuilder.code.CodeBlock;
import com.github.wrdlbrnft.codebuilder.code.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.code.If;
import com.github.wrdlbrnft.codebuilder.elements.Constructor;
import com.github.wrdlbrnft.codebuilder.elements.Field;
import com.github.wrdlbrnft.codebuilder.elements.Method;
import com.github.wrdlbrnft.codebuilder.elements.Type;
import com.github.wrdlbrnft.codebuilder.elements.Variable;
import com.github.wrdlbrnft.codebuilder.impl.ClassBuilder;
import com.github.wrdlbrnft.codebuilder.impl.Types;
import com.github.wrdlbrnft.codebuilder.impl.VariableGenerator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/01/15
 */
public abstract class AbsViewHolderBuilder {

    protected static class ViewTypeMismatchException extends Exception {

        private Type typeA;
        private Type typeB;

        public ViewTypeMismatchException(String message, Type typeA, Type typeB) {
            super(message);

            this.typeA = typeA;
            this.typeB = typeB;
        }

        public ViewTypeMismatchException(String message, Throwable cause, Type typeA, Type typeB) {
            super(message, cause);

            this.typeA = typeA;
            this.typeB = typeB;
        }

        public ViewTypeMismatchException(Throwable cause, Type typeA, Type typeB) {
            super(cause);

            this.typeA = typeA;
            this.typeB = typeB;
        }

        public Type getTypeA() {
            return typeA;
        }

        public Type getTypeB() {
            return typeB;
        }
    }

    protected interface Action {
        public void onBuild(CodeBlock code, Variable paramModel, VariableGenerator generator);
    }

    public static final Type TYPE_ABS_VIEW_HOLDER = Types.create("com.github.easyadapter.impl", "AbsViewHolder", EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT), Types.Android.RECYCLERVIEW_VIEWHOLDER);
    public static final Type TYPE_ABS_VIEW_HOLDER_FACTORY = Types.create("com.github.easyadapter.impl", "AbsViewHolderFactory", EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT), Types.OBJECT);
    public static final Type TYPE_INJECTOR = Types.create("com.github.easyadapter.EasyAdapter", "Injector", EnumSet.of(Modifier.PUBLIC), Types.OBJECT);
    public static final Type TYPE_VIEWMODEL = Types.create("com.github.easyadapter.api", "ViewModel", EnumSet.of(Modifier.PUBLIC), Types.OBJECT);

    private static final String FIELD_NAME_CURRENT_MODEL = "currentModel";

    protected final ProcessingEnvironment processingEnv;

    private final Type mModelType;
    private final TypeElement mElement;
    private final List<Action> mBindActions = new ArrayList<>();
    private final List<Action> mUnbindActions = new ArrayList<>();
    private final Map<Integer, List<Action>> mClickActions = new HashMap<>();
    private final Map<Integer, List<Action>> mCheckedChangeActions = new HashMap<>();
    private final Map<Type, String> mInjectedMap = new HashMap<>();
    private final Map<Type, Field> mFormaterMap = new HashMap<>();
    private final Map<Type, String> mFormaterInitialzerMap = new HashMap<>();

    private final Map<Integer, Type> mViewTypeMap = new HashMap<>();
    private final Map<Integer, String> mViewNameMap = new HashMap<>();

    private String mViewHolderName;

    private Method mMethodSetBackgroundSafely;
    private ClassBuilder mBuilder;

    protected AbsViewHolderBuilder(ProcessingEnvironment environment, TypeElement element) {
        processingEnv = environment;
        mElement = element;
        mModelType = Types.create(mElement);
    }

    protected Method setBackgroundSafely() {
        if (mMethodSetBackgroundSafely == null) {
            mMethodSetBackgroundSafely = mBuilder.addMethod(Types.Primitives.VOID, "setBackgroundSafely", EnumSet.of(Modifier.PRIVATE), new ExecutableBuilder() {

                private Variable paramDrawable;
                private Variable paramView;

                @Override
                public List<Variable> createParameterList(VariableGenerator generator) {
                    final List<Variable> parameters = new ArrayList<Variable>();

                    parameters.add(paramView = generator.generate(Types.Android.Views.VIEW));
                    parameters.add(paramDrawable = generator.generate(Types.Android.DRAWABLE));

                    return parameters;
                }

                @Override
                public void writeBody(CodeBlock code, VariableGenerator generator) {
                    If apiLevelIf = code.newIf("android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN");
                    apiLevelIf.whenTrue().append(paramView).append(".setBackground(").append(paramDrawable).append(");\n");
                    apiLevelIf.whenFalse().append(paramView).append(".setBackgroundDrawable(").append(paramDrawable).append(");\n");
                }
            });
        }

        return mMethodSetBackgroundSafely;
    }

    protected String requestInjectedObject(Type type) {
        if(type.isSubTypeOf(mModelType)) {
            return FIELD_NAME_CURRENT_MODEL;
        }

        if (type.isSubTypeOf(Types.Android.RECYCLERVIEW_VIEWHOLDER)) {
            return mViewHolderName + ".this";
        }

        final Set<Type> types = mInjectedMap.keySet();
        for (Type injectedType : types) {
            if (type.isSubTypeOf(injectedType)) {
                return mInjectedMap.get(injectedType);
            }

            if (injectedType.isSubTypeOf(type)) {
                final String name = mInjectedMap.get(type);
                mInjectedMap.remove(injectedType);
                mInjectedMap.put(type, name);
                return name;
            }
        }

        final String name = mBuilder.fieldGenerator().generateName();
        mInjectedMap.put(type, name);
        return name;
    }

    protected void queueBindAction(Action action) {
        mBindActions.add(action);
    }

    protected void queueUnbindAction(Action action) {
        mUnbindActions.add(action);
    }

    protected void queueClickAction(int id, Action action) {
        if (!mClickActions.containsKey(id)) {
            mClickActions.put(id, new ArrayList<Action>());
        }

        mClickActions.get(id).add(action);
    }

    protected void queueCheckedChangeAction(int id, Action action) {
        if (!mCheckedChangeActions.containsKey(id)) {
            mCheckedChangeActions.put(id, new ArrayList<Action>());
        }

        mCheckedChangeActions.get(id).add(action);
    }

    protected AnnotationValue getAnnotationValue(Element element, String annotationClass, String name) {

        final Map<? extends ExecutableElement, ? extends AnnotationValue> map = element.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(annotationClass))
                .map(AnnotationMirror::getElementValues)
                .findFirst().orElse(null);

        if (map != null) {
            final ExecutableElement key = map.keySet().stream()
                    .filter(k -> k.getSimpleName().toString().equals(name))
                    .findFirst().orElse(null);
            return map.get(key);
        }

        return null;
    }

    protected String executeMethod(Variable target, ExecutableElement method) {
        return executeMethod(target != null ? target.name() : null, method);
    }

    protected String executeMethod(ExecutableElement method) {
        return executeMethod((String)null, method);
    }

    protected String executeMethod(String target, ExecutableElement method) {
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

            final AnnotationValue viewIdValue = getAnnotationValue(parameter, Annotations.INJECT_VIEW, "value");
            if (viewIdValue != null) {
                final int viewId = (int) viewIdValue.getValue();
                try {
                    builder.append(view(viewId, parameterType));
                } catch (ViewTypeMismatchException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), method);
                }
            } else if (hasAnnotation(parameter, Annotations.INJECT)) {
                builder.append(requestInjectedObject(parameterType));
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not satisfy all the parameters of " + method.getSimpleName(), method);
            }
        }

        builder.append(")");

        return builder.toString();

    }

    protected String view(int id, Type type) throws ViewTypeMismatchException {

        if (mViewNameMap.containsKey(id)) {
            final Type registeredType = mViewTypeMap.get(id);

            if (!registeredType.isSubTypeOf(type)) {
                if (type.isSubTypeOf(registeredType)) {
                    mViewTypeMap.put(id, type);
                } else {
                    throw new ViewTypeMismatchException("A View is being assumed to have conflicting types!", registeredType, type);
                }
            }

            return mViewNameMap.get(id);
        } else {
            final String name = mBuilder.fieldGenerator().generateName();
            mViewNameMap.put(id, name);
            mViewTypeMap.put(id, type);

            return name;
        }
    }

    protected String formater(Type formaterType) {
        if (mFormaterMap.containsKey(formaterType)) {
            return mFormaterMap.get(formaterType).name();
        }

        final List<ExecutableElement> constructors = formaterType.getTypeElement().getEnclosedElements().stream()
                .filter(o -> o.getKind() == ElementKind.CONSTRUCTOR)
                .filter(o -> o instanceof ExecutableElement)
                .map(ExecutableElement.class::cast)
                .collect(Collectors.toList());

        if (constructors.size() > 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The Formater " + formaterType + " has more than one constructor!!1 Each Formater may only define one constructor!");
        } else if (constructors.size() < 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The Formater " + formaterType + " has more than no constructor!!1 Each Formater has to have exactly one constructor!");
        }

        final ExecutableElement constructorMethod = constructors.get(0);
        mFormaterInitialzerMap.put(formaterType, "new " + executeMethod(constructorMethod));

        final Field field = mBuilder.addField(formaterType, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
        mFormaterMap.put(formaterType, field);
        return field.name();
    }

    protected abstract void analyzeModel(TypeElement element);

    public void build() throws Exception {

        final int layoutId = getLayoutId(mElement);

        if (!implementsViewModel(mElement)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, mElement.getQualifiedName().toString() + " does not implement the ViewModel interface! All view models have to implement it.", mElement);
        }

        mViewHolderName = mModelType.className() + "$$ViewHolder";

        final Set<Type> implementedTypes = new HashSet<>();
        mBuilder = new ClassBuilder(processingEnv, mViewHolderName);
        mBuilder.setImplements(implementedTypes);
        mBuilder.setPackageName(mModelType.packageName());
        mBuilder.setExtends(TYPE_ABS_VIEW_HOLDER.genericVersion(mModelType));
        mBuilder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));

        analyzeModel(mElement);

        mBuilder.addMethod(Types.Primitives.VOID, "performBind", EnumSet.of(Modifier.PROTECTED), new ExecutableBuilder() {

            private Variable paramModel;

            @Override
            public List<Variable> createParameterList(VariableGenerator generator) {
                final List<Variable> params = new ArrayList<Variable>();

                params.add(paramModel = generator.generate(mModelType, Modifier.FINAL));

                return params;
            }

            @Override
            public void writeBody(CodeBlock code, VariableGenerator generator) {
                for (Action action : mBindActions) {
                    action.onBuild(code, paramModel, generator);
                }

                try {
                    final Set<Integer> checkedChangeViewIdSet = mCheckedChangeActions.keySet();
                    for (int viewId : checkedChangeViewIdSet) {
                        final List<Action> actions = mCheckedChangeActions.get(viewId);
                        final CodeBlock checkedChangeBlock = CodeBlock.Factory.newInstance();
                        for (Action action : actions) {
                            action.onBuild(checkedChangeBlock, paramModel, generator);
                        }

                        code.append(view(viewId, Types.Android.Views.CHECKBOX)).append(".setOnCheckedChangeListener(new android.widget.CheckBox.OnCheckedChangeListener() {\n");
                        code.append("\t@Override\n");
                        code.append("\tpublic void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {\n");
                        code.append(checkedChangeBlock);
                        code.append("\t}\n");
                        code.append("});\n");
                    }

                    final Set<Integer> clickViewIdSet = mClickActions.keySet();
                    for (int viewId : clickViewIdSet) {
                        final List<Action> actions = mClickActions.get(viewId);
                        final CodeBlock clickBlock = CodeBlock.Factory.newInstance();
                        for (Action action : actions) {
                            action.onBuild(clickBlock, paramModel, generator);
                        }

                        code.append(view(viewId, Types.Android.Views.VIEW)).append(".setOnClickListener(new android.view.View.OnClickListener() {\n");
                        code.append("\t@Override\n");
                        code.append("\tpublic void onClick(android.view.View view) {\n");
                        code.append(clickBlock);
                        code.append("\t}\n");
                        code.append("});\n");
                    }
                } catch (ViewTypeMismatchException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), mElement);
                }
            }
        });

        mBuilder.addMethod(Types.Primitives.VOID, "performUnbind", EnumSet.of(Modifier.PROTECTED), new ExecutableBuilder() {

            private Variable paramModel;

            @Override
            public List<Variable> createParameterList(VariableGenerator generator) {
                final List<Variable> params = new ArrayList<Variable>();

                params.add(paramModel = generator.generate(mModelType, Modifier.FINAL));

                return params;
            }

            @Override
            public void writeBody(CodeBlock code, VariableGenerator generator) {
                for (Action action : mUnbindActions) {
                    action.onBuild(code, paramModel, generator);
                }

                try {
                    final Set<Integer> checkedChangeViewIdSet = mCheckedChangeActions.keySet();
                    for (int viewId : checkedChangeViewIdSet) {
                        code.append(view(viewId, Types.Android.Views.CHECKBOX)).append(".setOnCheckedChangeListener(null);\n");
                    }

                    final Set<Integer> clickViewIdSet = mClickActions.keySet();
                    for (int viewId : clickViewIdSet) {
                        code.append(view(viewId, Types.Android.Views.VIEW)).append(".setOnClickListener(null);\n");
                    }
                } catch (ViewTypeMismatchException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was " + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(), mElement);
                }
            }
        });

        final Map<Type, Field> injectedFieldMap = new HashMap<>();
        final List<Type> injectedTypes = new ArrayList<>(mInjectedMap.keySet());
        for (Type type : injectedTypes) {
            final String name = mInjectedMap.get(type);
            final Field field = mBuilder.addField(type, name, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
            injectedFieldMap.put(type, field);
        }

        final Constructor constructor = mBuilder.addConstructor(EnumSet.of(Modifier.PUBLIC), new ExecutableBuilder() {

            private final Map<Type, Variable> injectedParameterMap = new HashMap<>();
            private Variable paramView;

            @Override
            public List<Variable> createParameterList(VariableGenerator generator) {
                final List<Variable> params = new ArrayList<Variable>();

                params.add(paramView = generator.generate(Types.Android.Views.VIEW));

                for (Type type : injectedTypes) {
                    final Variable parameter = generator.generate(type);
                    injectedParameterMap.put(type, parameter);
                    params.add(parameter);
                }

                return params;
            }

            @Override
            public void writeBody(CodeBlock code, VariableGenerator generator) {
                code.append("super(").append(paramView).append(");\n");

                for (Type type : injectedTypes) {
                    final Variable parameter = injectedParameterMap.get(type);
                    final Field field = injectedFieldMap.get(type);
                    code.append(field.set(parameter)).append(";\n");
                }
            }
        });

        final Set<Integer> viewIdSet = mViewNameMap.keySet();
        for (int viewId : viewIdSet) {
            final String name = mViewNameMap.get(viewId);
            final Type type = mViewTypeMap.get(viewId);
            final Field field = mBuilder.addField(type, name, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
            if (type.equals(Types.Android.Views.VIEW)) {
                constructor.code().append(field.set("itemView.findViewById(" + viewId + ");\n"));
            } else {
                constructor.code().append(field.set("(" + type + ") itemView.findViewById(" + viewId + ");\n"));
            }
        }

        final Set<Type> formaterTypes = mFormaterMap.keySet();
        for (Type formaterType : formaterTypes) {
            final Field formaterField = mFormaterMap.get(formaterType);
            final String initialValue = mFormaterInitialzerMap.get(formaterType);
            constructor.code().append(formaterField.set(initialValue)).append(";\n");
        }

        final Type viewHolderType = mBuilder.build();

        final String factoryClassName = mModelType.className() + "$$ViewHolderFactory";
        final ClassBuilder factoryBuilder = new ClassBuilder(processingEnv, factoryClassName);
        factoryBuilder.setPackageName(mModelType.packageName());
        factoryBuilder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));
        factoryBuilder.setExtends(TYPE_ABS_VIEW_HOLDER_FACTORY.genericVersion(mModelType));

        factoryBuilder.addImport(viewHolderType);
        factoryBuilder.addImport(Types.Android.Views.VIEW);

        final Field fieldInflater = factoryBuilder.addField(Types.Android.LAYOUT_INFLATER, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));

        final Map<Type, Field> factoryFieldMap = new HashMap<>();
        for (Type type : injectedTypes) {
            final Field field = factoryBuilder.addField(type, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
            factoryFieldMap.put(type, field);
        }

        factoryBuilder.addConstructor(EnumSet.of(Modifier.PUBLIC), new ExecutableBuilder() {

            private Variable paramInflater;
            private Variable paramInjector;

            @Override
            public List<Variable> createParameterList(VariableGenerator generator) {
                final List<Variable> parameters = new ArrayList<Variable>();

                parameters.add(paramInflater = generator.generate(Types.Android.LAYOUT_INFLATER));
                parameters.add(paramInjector = generator.generate(TYPE_INJECTOR));

                return parameters;
            }

            @Override
            public void writeBody(CodeBlock code, VariableGenerator generator) {
                code.append("super(").append(paramInflater).append(", ").append(paramInjector).append(");\n");
                code.append(fieldInflater.set(paramInflater)).append(";\n");

                for (Type type : injectedTypes) {
                    final Field field = factoryFieldMap.get(type);
                    code.append(field.set(paramInjector + ".get(" + type + ".class" + ")")).append(";\n");
                }
            }
        });

        factoryBuilder.addMethod(TYPE_ABS_VIEW_HOLDER.genericVersion(mModelType), "newInstance", EnumSet.of(Modifier.PUBLIC), new ExecutableBuilder() {

            private Variable paramViewGroup;

            @Override
            public List<Variable> createParameterList(VariableGenerator generator) {
                final List<Variable> parameters = new ArrayList<Variable>();

                parameters.add(paramViewGroup = generator.generate(Types.Android.Views.VIEW_GROUP));

                return parameters;
            }

            @Override
            public void writeBody(CodeBlock code, VariableGenerator generator) {
                Variable varView = generator.generate(Types.Android.Views.VIEW, Modifier.FINAL);
                code.append(varView.initialize()).append(" = ").append(fieldInflater).append(".inflate(").append(layoutId).append(", ").append(paramViewGroup).append(", ").append(false).append(");\n");
                code.append("return new ").append(viewHolderType).append("(").append(varView);

                for (Type type : injectedTypes) {
                    final Field field = factoryFieldMap.get(type);
                    code.append(", ").append(field);
                }

                code.append(");\n");
            }
        });

        factoryBuilder.build();
    }

    protected boolean hasAnnotation(Element element, String annotationClass) {
        final List<? extends AnnotationMirror> mirrors = element.getAnnotationMirrors();
        for (AnnotationMirror mirror : mirrors) {
            if (mirror.getAnnotationType().toString().equals(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    private boolean implementsViewModel(TypeElement element) {
        for (TypeMirror mirror : element.getInterfaces()) {
            if (mirror.toString().equals(TYPE_VIEWMODEL.fullClassName())) {
                return true;
            }
        }
        return false;
    }

    private int getLayoutId(TypeElement model) throws Exception {
        for (AnnotationMirror mirror : model.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(Annotations.LAYOUT)) {
                return (int) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
            }
        }
        throw new Exception("@Layout Annotation is missing!");
    }
}
