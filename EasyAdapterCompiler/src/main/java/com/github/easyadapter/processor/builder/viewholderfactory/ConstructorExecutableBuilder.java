package com.github.easyadapter.processor.builder.viewholderfactory;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.ExecutableBuilder;
import com.github.easyadapter.codewriter.elements.Field;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.processor.DefinedTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
class ConstructorExecutableBuilder implements ExecutableBuilder {

    private final Field mFieldInflater;
    private final List<Type> mInjectedTypes;
    private final Map<Type, Field> mFactoryFieldMap;

    private Variable paramInflater;
    private Variable paramInjector;

    ConstructorExecutableBuilder(Field fieldInflater, List<Type> injectedTypes, Map<Type, Field> factoryFieldMap) {
        mFieldInflater = fieldInflater;
        mInjectedTypes = injectedTypes;
        mFactoryFieldMap = factoryFieldMap;
    }

    @Override
    public List<Variable> createParameterList(VariableGenerator generator) {
        final List<Variable> parameters = new ArrayList<>();

        parameters.add(paramInflater = generator.generate(Types.Android.LAYOUT_INFLATER));
        parameters.add(paramInjector = generator.generate(DefinedTypes.INJECTOR));

        return parameters;
    }

    @Override
    public void writeBody(CodeBlock code, VariableGenerator generator) {
        code.append("super(").append(paramInflater).append(", ").append(paramInjector).append(");\n");
        code.append(mFieldInflater.set(paramInflater)).append(";\n");

        for (Type type : mInjectedTypes) {
            final Field field = mFactoryFieldMap.get(type);
            code.append(field.set(paramInjector + ".get(" + type + ".class" + ")")).append(";\n");
        }
    }
}
