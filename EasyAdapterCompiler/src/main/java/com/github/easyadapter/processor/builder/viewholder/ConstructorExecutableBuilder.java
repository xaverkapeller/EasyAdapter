package com.github.easyadapter.processor.builder.viewholder;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.ExecutableBuilder;
import com.github.easyadapter.codewriter.elements.Field;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 30/05/15
 */
class ConstructorExecutableBuilder implements ExecutableBuilder {

    private final Map<Type, Variable> mInjectedParameterMap = new HashMap<>();
    private final List<Type> mConstructorParameters;
    private final Map<Type, Field> mFieldMap;

    private Variable mParamView;

    public ConstructorExecutableBuilder(List<Type> constructorParameters, Map<Type, Field> fieldMap) {
        mConstructorParameters = constructorParameters;
        mFieldMap = fieldMap;
    }

    @Override
    public List<Variable> createParameterList(VariableGenerator generator) {
        final List<Variable> params = new ArrayList<Variable>();

        params.add(mParamView = generator.generate(Types.Android.Views.VIEW));

        for (Type type : mConstructorParameters) {
            final Variable parameter = generator.generate(type);
            mInjectedParameterMap.put(type, parameter);
            params.add(parameter);
        }

        return params;
    }

    @Override
    public void writeBody(CodeBlock code, VariableGenerator generator) {
        code.append("super(").append(mParamView).append(");\n");

        for (Type type : mConstructorParameters) {
            final Variable parameter = mInjectedParameterMap.get(type);
            final Field field = mFieldMap.get(type);
            code.append(field.set(parameter)).append(";\n");
        }
    }
}
