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
class PerformBindExecutableBuilder implements ExecutableBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;
    private final AnalyzerResult mAnalyzerResult;

    private Variable paramModel;

    PerformBindExecutableBuilder(ProcessingEnvironment processingEnvironment, AnalyzerResult analyzerResult) {
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
        for (Action action : mAnalyzerResult.getBindActions()) {
            action.onBuild(code, paramModel, generator);
        }

        try {
            final Resolver resolver = mAnalyzerResult.getResolver();
            handleCheckedChangeActions(code, generator, resolver);
            handleClickActions(code, generator, resolver);
        } catch (ViewTypeMismatchException e) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "You are assuming the same View to have two conflicting types! You previously assumed it was "
                            + e.getTypeA() + " but now you are assuming it is a " + e.getTypeB(),
                    mAnalyzerResult.getModelTypeElement());
        }
    }

    private void handleCheckedChangeActions(CodeBlock code, VariableGenerator generator, Resolver resolver) throws ViewTypeMismatchException {
        final Map<Integer, List<Action>> checkedChangeActions = mAnalyzerResult.getCheckedChangeActions();
        final Set<Integer> checkedChangeViewIdSet = checkedChangeActions.keySet();
        for (int viewId : checkedChangeViewIdSet) {
            final List<Action> actions = checkedChangeActions.get(viewId);
            final CodeBlock checkedChangeBlock = CodeBlock.Factory.newInstance();
            for (Action action : actions) {
                action.onBuild(checkedChangeBlock, paramModel, generator);
            }

            code.append(resolver.view(viewId, Types.Android.Views.CHECKBOX)).append(".setOnCheckedChangeListener(new android.widget.CheckBox.OnCheckedChangeListener() {\n");
            code.append("\t@Override\n");
            code.append("\tpublic void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {\n");
            code.append(checkedChangeBlock);
            code.append("\t}\n");
            code.append("});\n");
        }
    }

    private void handleClickActions(CodeBlock code, VariableGenerator generator, Resolver resolver) throws ViewTypeMismatchException {
        final Map<Integer, List<Action>> clickActions = mAnalyzerResult.getClickActions();
        final Set<Integer> clickViewIdSet = clickActions.keySet();
        for (int viewId : clickViewIdSet) {
            final List<Action> actions = clickActions.get(viewId);
            final CodeBlock clickBlock = CodeBlock.Factory.newInstance();
            for (Action action : actions) {
                action.onBuild(clickBlock, paramModel, generator);
            }

            code.append(resolver.view(viewId, Types.Android.Views.VIEW)).append(".setOnClickListener(new android.view.View.OnClickListener() {\n");
            code.append("\t@Override\n");
            code.append("\tpublic void onClick(android.view.View view) {\n");
            code.append(clickBlock);
            code.append("\t}\n");
            code.append("});\n");
        }
    }
}
