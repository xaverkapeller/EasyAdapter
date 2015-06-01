package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.processor.builder.resolver.Resolver;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
class AnalyzerResultImpl implements AnalyzerResult {

    private final int mLayoutId;
    private final Type mModelType;
    private final TypeElement mTypeElement;
    private final Resolver mResolver;
    private final String mViewHolderName;
    private final String mViewHolderFactoryName;
    private final List<Action> mBindActions;
    private final List<Action> mUnbindActions;
    private final Map<Integer, List<Action>> mClickActions;
    private final Map<Integer, List<Action>> mCheckedChangeActions;

    public AnalyzerResultImpl(int layoutId, Type modelType, TypeElement typeElement, String viewHolderName, String viewHolderFactoryName, List<Action> bindActions, List<Action> unbindActions, Map<Integer, List<Action>> checkedChangeActions, Map<Integer, List<Action>> clickActions, Resolver resolver) {
        mLayoutId = layoutId;
        mModelType = modelType;
        mTypeElement = typeElement;
        mResolver = resolver;
        mViewHolderName = viewHolderName;
        mViewHolderFactoryName = viewHolderFactoryName;
        mBindActions = Collections.unmodifiableList(bindActions);
        mUnbindActions = Collections.unmodifiableList(unbindActions);
        mClickActions = Collections.unmodifiableMap(clickActions);
        mCheckedChangeActions = Collections.unmodifiableMap(checkedChangeActions);
    }

    @Override
    public int getLayoutId() {
        return mLayoutId;
    }

    @Override
    public Map<Integer, List<Action>> getClickActions() {
        return mClickActions;
    }

    @Override
    public Map<Integer, List<Action>> getCheckedChangeActions() {
        return mCheckedChangeActions;
    }

    @Override
    public List<Action> getBindActions() {
        return mBindActions;
    }

    @Override
    public List<Action> getUnbindActions() {
        return mUnbindActions;
    }

    @Override
    public String getViewHolderName() {
        return mViewHolderName;
    }

    @Override
    public String getViewHolderFactoryName() {
        return mViewHolderFactoryName;
    }

    @Override
    public Type getModelType() {
        return mModelType;
    }

    @Override
    public TypeElement getModelTypeElement() {
        return mTypeElement;
    }

    @Override
    public Resolver getResolver() {
        return mResolver;
    }
}
