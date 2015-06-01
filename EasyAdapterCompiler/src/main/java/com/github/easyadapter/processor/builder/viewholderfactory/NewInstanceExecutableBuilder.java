package com.github.easyadapter.processor.builder.viewholderfactory;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.ExecutableBuilder;
import com.github.easyadapter.codewriter.elements.Field;
import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
class NewInstanceExecutableBuilder implements ExecutableBuilder {

    private final Field mFieldInflater;
    private final int mLayoutId;
    private final Type mViewHolderType;
    private final List<Type> mInjectedTypes;
    private final Map<Type, Field> mFactoryFieldMap;

    private Variable paramViewGroup;

    NewInstanceExecutableBuilder(Field fieldInflater, int layoutId, Type viewHolderType, List<Type> injectedTypes, Map<Type, Field> factoryFieldMap) {
        mFieldInflater = fieldInflater;
        mLayoutId = layoutId;
        mViewHolderType = viewHolderType;
        mInjectedTypes = injectedTypes;
        mFactoryFieldMap = factoryFieldMap;
    }

    @Override
    public List<Variable> createParameterList(VariableGenerator generator) {
        final List<Variable> parameters = new ArrayList<>();

        parameters.add(paramViewGroup = generator.generate(Types.Android.Views.VIEW_GROUP));

        return parameters;
    }

    @Override
    public void writeBody(CodeBlock code, VariableGenerator generator) {
        Variable varView = generator.generate(Types.Android.Views.VIEW, Modifier.FINAL);
        code.append(varView.initialize()).append(" = ").append(mFieldInflater).append(".inflate(").append(mLayoutId).append(", ").append(paramViewGroup).append(", ").append(false).append(");\n");
        code.append("return new ").append(mViewHolderType).append("(").append(varView);

        for (Type type : mInjectedTypes) {
            final Field field = mFactoryFieldMap.get(type);
            code.append(", ").append(field);
        }

        code.append(");\n");
    }
}
