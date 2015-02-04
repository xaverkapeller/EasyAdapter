package com.github.easyadapter.builder.impl;
import com.github.easyadapter.builder.api.builder.ExecutableBuilder;
import com.github.easyadapter.builder.api.elements.Constructor;
import com.github.easyadapter.builder.api.elements.Field;
import com.github.easyadapter.builder.api.elements.Method;
import com.github.easyadapter.builder.api.elements.Type;
import com.github.easyadapter.builder.api.elements.Variable;
import com.github.easyadapter.utils.Utils;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public class ClassBuilder {

    private final ProcessingEnvironment mEnvironment;
    private String mClassName;
    private String mPackageName = "";
    private Set<Modifier> mModifiers = new HashSet<Modifier>();
    private Type mExtends = null;
    private Set<Type> mImplements = new HashSet<Type>();

    private final VariableGenerator mFieldGenerator = new VariableGenerator();

    private final Set<FieldImpl> mFields = new HashSet<FieldImpl>();
    private final Set<MethodImpl> mMethods = new HashSet<MethodImpl>();
    private final Set<ConstructorImpl> mConstructors = new HashSet<ConstructorImpl>();

    public ClassBuilder(ProcessingEnvironment environment, String className) {
        mEnvironment = environment;
        mClassName = className;
        mFieldGenerator.setPrefix("_");
    }

    public VariableGenerator fieldGenerator() {
        return mFieldGenerator;
    }

    public ClassBuilder setPackageName(String packageName) {
        mPackageName = packageName;
        return this;
    }

    public ClassBuilder setClassName(String className) {
        mClassName = className;
        return this;
    }

    public ClassBuilder setModifiers(Set<Modifier> modifiers) {
        mModifiers = modifiers;
        return this;
    }

    public ClassBuilder setExtends(Type type) {
        mExtends = type;
        return this;
    }

    public ClassBuilder setImplements(Set<Type> types) {
        mImplements = types;
        return this;
    }

    public Field addField(Type type, String name, Set<Modifier> modifiers) {
        final FieldImpl field = new FieldImpl(name, type, modifiers);
        mFields.add(field);
        return field;
    }

    public Field addField(Type type, Set<Modifier> modifiers) {
        final FieldImpl field = new FieldImpl(mFieldGenerator.generateName(), type, modifiers);
        mFields.add(field);
        return field;
    }

    public Method addMethod(Type returnType, String name, Set<Modifier> modifiers, List<Variable> parameters) {
        final MethodImpl method = new MethodImpl(returnType, name, parameters, modifiers);
        mMethods.add(method);
        return method;
    }

    public Method addMethod(Type returnType, String name, Set<Modifier> modifiers, ExecutableBuilder builder) {
        final VariableGenerator generator = new VariableGenerator();
        final List<Variable> parameters = builder.createParameterList(generator);
        final MethodImpl method = new MethodImpl(returnType, name, parameters, modifiers);
        mMethods.add(method);
        builder.writeBody(method.code(), generator);
        return method;
    }

    public Constructor addConstructor(Set<Modifier> modifiers, List<Variable> parameters) {
        final ConstructorImpl constructor = new ConstructorImpl(parameters, modifiers);
        mConstructors.add(constructor);
        return constructor;
    }

    public Method implementMethod(ExecutableElement method, ExecutableBuilder builder) {
        final String name = method.getSimpleName().toString();
        final Type returnType = Types.create(method.getReturnType());
        return addMethod(returnType, name, EnumSet.of(Modifier.PUBLIC), builder);
    }

    public Constructor addConstructor(Set<Modifier> modifiers, ExecutableBuilder builder) {
        final VariableGenerator generator = new VariableGenerator();
        final List<Variable> parameters = builder.createParameterList(generator);
        final ConstructorImpl constructor = new ConstructorImpl(parameters, modifiers);
        mConstructors.add(constructor);
        builder.writeBody(constructor.code(), generator);
        return constructor;
    }

    private String[] getImplementedInterfacesArray() {
        final int count = mImplements.size();
        final String[] array = new String[count];

        int i = 0;
        for (Type type : mImplements) {
            array[i++] = type.fullClassName();
        }

        return array;
    }

    public Type build() throws IOException {
        final JavaWriter writer = Utils.createWriter(mEnvironment, mClassName);
        writer.emitPackage(mPackageName);

        final String[] implementedInterfaces = getImplementedInterfacesArray();

        writer.beginType(mClassName, "class", EnumSet.of(Modifier.PUBLIC, Modifier.FINAL), mExtends != null ? mExtends.fullClassName() : null, implementedInterfaces);

        for (FieldImpl field : mFields) {
            final String type = field.type().fullClassName();
            final String name = field.name();
            final Set<Modifier> modifiers = field.modifiers();
            final String initialValue = field.initialValue();

            if(initialValue != null) {
                writer.emitField(type, name, modifiers, initialValue);
            } else {
                writer.emitField(type, name, modifiers);
            }
        }

        for (ConstructorImpl constructor : mConstructors) {
            final Set<Modifier> modifiers = constructor.modifiers();
            final List<Variable> parameters = constructor.parameters();

            final String[] parameterArray = new String[parameters.size() * 2];

            for (int i = 0, length = parameters.size(); i < length; i++) {
                final Variable parameter = parameters.get(i);
                final Type type = parameter.type();

                if(parameter.modifiers().contains(Modifier.FINAL)) {
                    parameterArray[i * 2] = "final " + type.fullClassName();
                    parameterArray[i * 2 + 1] = parameter.name();
                } else {
                    parameterArray[i * 2] = type.fullClassName();
                    parameterArray[i * 2 + 1] = parameter.name();
                }
            }

            writer.beginConstructor(modifiers, parameterArray);

            final StringBuilder builder = new StringBuilder();
            constructor.code().write(builder);
            writer.emitStatement(builder.toString());
            writer.endConstructor();
        }

        for(MethodImpl method : mMethods) {
            final String name = method.name();
            final String returnType = method.returnType() != null ? method.returnType().fullClassName() : "void";
            final Set<Modifier> modifiers = method.modifiers();
            final List<Variable> parameters = method.parameters();

            final String[] parameterArray = new String[parameters.size() * 2];

            for (int i = 0, length = parameters.size(); i < length; i++) {
                final Variable parameter = parameters.get(i);
                final Type type = parameter.type();

                if(parameter.modifiers().contains(Modifier.FINAL)) {
                    parameterArray[i * 2] = "final " + type.fullClassName();
                    parameterArray[i * 2 + 1] = parameter.name();
                } else {
                    parameterArray[i * 2] = type.fullClassName();
                    parameterArray[i * 2 + 1] = parameter.name();
                }
            }

            writer.beginMethod(returnType, name, modifiers, parameterArray);

            final StringBuilder builder = new StringBuilder();
            method.code().write(builder);

            writer.emitStatement(builder.toString());
            writer.endMethod();
        }

        writer.endType();
        writer.close();

        return new TypeImpl(mPackageName, mClassName, mModifiers, mExtends != null ? mExtends : Types.OBJECT);
    }
}
