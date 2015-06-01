package com.github.easyadapter.processor.builder.viewholder;

import com.github.easyadapter.codewriter.elements.Constructor;
import com.github.easyadapter.codewriter.elements.Field;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.impl.ClassBuilder;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.processor.DefinedTypes;
import com.github.easyadapter.processor.builder.analyzer.AnalyzerResult;
import com.github.easyadapter.processor.builder.resolver.Resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/01/15
 */
public class ViewHolderBuilder {

    protected final ProcessingEnvironment mProcessingEnvironment;

    public ViewHolderBuilder(ProcessingEnvironment environment) {
        mProcessingEnvironment = environment;
    }

    public ViewHolderInfo build(AnalyzerResult result) throws IOException {
        final Type modelType = result.getModelType();

        final Set<Type> implementedTypes = new HashSet<>();
        final ClassBuilder builder = new ClassBuilder(mProcessingEnvironment, result.getViewHolderName());
        builder.setImplements(implementedTypes);
        builder.setPackageName(modelType.packageName());
        builder.setExtends(DefinedTypes.ABS_VIEW_HOLDER.genericVersion(modelType));
        builder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));

        builder.addMethod(Types.Primitives.VOID, "performBind", EnumSet.of(Modifier.PROTECTED), new PerformBindExecutableBuilder(mProcessingEnvironment, result));
        builder.addMethod(Types.Primitives.VOID, "performUnbind", EnumSet.of(Modifier.PROTECTED), new PerformUnbindExecutableBuilder(mProcessingEnvironment, result));

        final Resolver resolver = result.getResolver();

        final Map<Type, Field> injectedFieldMap = new HashMap<>();
        final Map<Type, String> injectedTypeFieldNameMap = resolver.getInjectedTypeFieldNameMap();
        final List<Type> constructorParameters = new ArrayList<>(injectedTypeFieldNameMap.keySet());
        for (Type type : constructorParameters) {
            final String name = injectedTypeFieldNameMap.get(type);
            final Field field = builder.addField(type, name, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
            injectedFieldMap.put(type, field);
        }

        final Constructor constructor = builder.addConstructor(EnumSet.of(Modifier.PUBLIC), new ConstructorExecutableBuilder(constructorParameters, injectedFieldMap));

        final Map<Integer, String> viewFieldNameMap = resolver.getViewFieldNameMap();
        final Map<Integer, Type> viewTypeMap = resolver.getViewTypeMap();
        final Set<Integer> viewIdSet = viewFieldNameMap.keySet();
        for (int viewId : viewIdSet) {
            final String name = viewFieldNameMap.get(viewId);
            final Type type = viewTypeMap.get(viewId);
            final Field field = builder.addField(type, name, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
            if (type.equals(Types.Android.Views.VIEW)) {
                constructor.code().append(field.set("itemView.findViewById(" + viewId + ");\n"));
            } else {
                constructor.code().append(field.set("(" + type + ") itemView.findViewById(" + viewId + ");\n"));
            }
        }

        final Map<Type, String> formaterFieldNameMap = resolver.getFormaterFieldNameMap();
        final Map<Type, String> formaterFieldInitializerMap = resolver.getFormaterFieldInitializerMap();

        final Set<Type> formaterTypes = formaterFieldNameMap.keySet();
        for (Type formaterType : formaterTypes) {
            final String fieldName = formaterFieldNameMap.get(formaterType);
            final String initialValue = formaterFieldInitializerMap.get(formaterType);

            final Field formaterField = builder.addField(formaterType, fieldName, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
            constructor.code().append(formaterField.set(initialValue)).append(";\n");
        }

        final Type viewHolderType = builder.build();
        return new ViewHolderInfoImpl(viewHolderType, constructorParameters);
    }
}
