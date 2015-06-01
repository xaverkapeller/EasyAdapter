package com.github.easyadapter.processor.builder.viewholderfactory;

import com.github.easyadapter.codewriter.elements.Field;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.impl.ClassBuilder;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.processor.DefinedTypes;
import com.github.easyadapter.processor.builder.analyzer.AnalyzerResult;
import com.github.easyadapter.processor.builder.viewholder.ViewHolderInfo;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
public class ViewModelFactoryBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;

    public ViewModelFactoryBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public void build(AnalyzerResult analyzerResult, ViewHolderInfo viewHolderInfo) throws IOException {
        final Type viewHolderType = viewHolderInfo.getViewHolderType();
        final List<Type> constructorParameters = viewHolderInfo.getConstructorParameters();
        final String factoryName = analyzerResult.getViewHolderFactoryName();
        final Type modelType = analyzerResult.getModelType();

        final ClassBuilder factoryBuilder = new ClassBuilder(mProcessingEnvironment, factoryName);
        factoryBuilder.setPackageName(modelType.packageName());
        factoryBuilder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));
        factoryBuilder.setExtends(DefinedTypes.ABS_VIEW_HOLDER_FACTORY.genericVersion(modelType));

        factoryBuilder.addImport(viewHolderInfo.getViewHolderType());
        factoryBuilder.addImport(Types.Android.Views.VIEW);

        final Field fieldInflater = factoryBuilder.addField(Types.Android.LAYOUT_INFLATER, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));

        final Map<Type, Field> factoryFieldMap = new HashMap<>();
        for (Type type : constructorParameters) {
            final Field field = factoryBuilder.addField(type, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
            factoryFieldMap.put(type, field);
        }

        factoryBuilder.addConstructor(EnumSet.of(Modifier.PUBLIC), new ConstructorExecutableBuilder(fieldInflater, constructorParameters, factoryFieldMap));

        factoryBuilder.addMethod(DefinedTypes.ABS_VIEW_HOLDER.genericVersion(modelType), "newInstance", EnumSet.of(Modifier.PUBLIC),
                new NewInstanceExecutableBuilder(fieldInflater, analyzerResult.getLayoutId(), viewHolderType, constructorParameters, factoryFieldMap));

        factoryBuilder.build();
    }
}
