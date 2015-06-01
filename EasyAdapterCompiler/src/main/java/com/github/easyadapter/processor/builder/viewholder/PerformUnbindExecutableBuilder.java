package com.github.easyadapter.processor.builder.viewholder;

import com.github.easyadapter.codewriter.code.CodeBlock;
import com.github.easyadapter.codewriter.code.ExecutableBuilder;
import com.github.easyadapter.codewriter.elements.Variable;
import com.github.easyadapter.codewriter.impl.Types;
import com.github.easyadapter.codewriter.impl.VariableGenerator;
import com.github.easyadapter.processor.builder.analyzer.Action;
import com.github.easyadapter.processor.builder.analyzer.AnalyzerResult;
import com.github.easyadapter.processor.builder.resolver.Resolver;
import com.github.easyadapter.processor.builder.exceptions.ViewTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 30/05/15
 */
class PerformUnbindExecutableBuilder implements ExecutableBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;
    private final AnalyzerResult mAnalyzerResult;

    private Variable paramModel;

    PerformUnbindExecutableBuilder(ProcessingEnvironment processingEnvironment, AnalyzerResult analyzerResult) {
        mProcessingEnvironment = processingEnvironment;
        mAnalyzerResult = analyzerResult;
    }

    @Override
    public List<Variable> createParameterList(VariableGenerator generator) {
        final List<Variable> params = new ArrayList<Variable>();

        params.add(paramModel = generator.generate(mAnalyzerResult.getModelType(), Modifier.FINAL));

        return params;
    }

    @Override
    public void writeBody(CodeBlock code, VariableGenerator generator) {
        for (Action action : mAnalyzerResult.getUnbindActions()) {
            action.onBuild(code, paramModel, generator);
        }

        try {
            final Resolver resolver = mAnalyzerResult.getResolver();
            handleCheckedChangeActions(code, resolver);
            handleClickActions(code, resolver);
        } catch (ViewTypeMismatchException e) {
            mProcessingEnvironment.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was "
                                    + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(),
                            mAnalyzerResult.getModelTypeElement());
        }
    }

    private void handleCheckedChangeActions(CodeBlock code, Resolver resolver) throws ViewTypeMismatchException {
        final Map<Integer, List<Action>> checkedChangeActions = mAnalyzerResult.getCheckedChangeActions();
        final Set<Integer> checkedChangeViewIdSet = checkedChangeActions.keySet();
        for (int viewId : checkedChangeViewIdSet) {
            code.append(resolver.view(viewId, Types.Android.Views.CHECKBOX)).append(".setOnCheckedChangeListener(null);\n");
        }
    }

    private void handleClickActions(CodeBlock code, Resolver resolver) throws ViewTypeMismatchException {
        final Map<Integer, List<Action>> clickActions = mAnalyzerResult.getClickActions();
        final Set<Integer> clickViewIdSet = clickActions.keySet();
        for (int viewId : clickViewIdSet) {
            code.append(resolver.view(viewId, Types.Android.Views.VIEW)).append(".setOnClickListener(null);\n");
        }
    }
}
