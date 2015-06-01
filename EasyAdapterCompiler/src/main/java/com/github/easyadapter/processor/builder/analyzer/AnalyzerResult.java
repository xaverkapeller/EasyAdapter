package com.github.easyadapter.processor.builder.analyzer;

import com.github.easyadapter.codewriter.elements.Type;
import com.github.easyadapter.processor.builder.resolver.Resolver;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 31/05/15
 */
public interface AnalyzerResult {
    public int getLayoutId();
    public Map<Integer, List<Action>> getClickActions();
    public Map<Integer, List<Action>> getCheckedChangeActions();
    public List<Action> getBindActions();
    public List<Action> getUnbindActions();
    public String getViewHolderName();
    public String getViewHolderFactoryName();
    public Type getModelType();
    public TypeElement getModelTypeElement();
    public Resolver getResolver();
}
