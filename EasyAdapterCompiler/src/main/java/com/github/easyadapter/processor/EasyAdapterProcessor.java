package com.github.easyadapter.processor;

import com.github.easyadapter.codewriter.utils.Utils;
import com.github.easyadapter.processor.builder.analyzer.AnalyzerResult;
import com.github.easyadapter.processor.builder.analyzer.ViewModelAnalyzer;
import com.github.easyadapter.processor.builder.viewholder.ViewHolderBuilder;
import com.github.easyadapter.processor.builder.viewholder.ViewHolderInfo;
import com.github.easyadapter.processor.builder.viewholderfactory.ViewModelFactoryBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class EasyAdapterProcessor extends AbstractProcessor {

    ViewModelAnalyzer mViewModelAnalyzer;
    ViewHolderBuilder mViewHolderBuilder;
    ViewModelFactoryBuilder mViewHolderFactoryBuilder;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mViewModelAnalyzer = new ViewModelAnalyzer(processingEnv);
        mViewHolderBuilder = new ViewHolderBuilder(processingEnv);
        mViewHolderFactoryBuilder = new ViewModelFactoryBuilder(processingEnv);

        TypeElement layoutAnnotation = null;
        TypeElement listenerAnnotation = null;
        for (TypeElement annotation : annotations) {
            final String annotationClassName = annotation.asType().toString();
            if (annotationClassName.equals(Annotations.LAYOUT)) {
                layoutAnnotation = annotation;
            } else if (annotationClassName.equals(Annotations.LISTENER)) {
                listenerAnnotation = annotation;
            }
        }

        if (layoutAnnotation != null) {
            final List<TypeElement> listeners = new ArrayList<>();
            if (listenerAnnotation != null) {
                final Set<? extends Element> elementsWithListenerAnnotation = roundEnv.getElementsAnnotatedWith(listenerAnnotation);
                for (Element element : elementsWithListenerAnnotation) {
                    if (element instanceof TypeElement) {
                        final TypeElement listener = (TypeElement) element;
                        listeners.add(listener);
                    }
                }
            }

            final Set<? extends Element> elementsWithLayoutAnnotation = roundEnv.getElementsAnnotatedWith(layoutAnnotation);
            for (Element element : elementsWithLayoutAnnotation) {
                if (element instanceof TypeElement) {
                    final TypeElement viewModelType = (TypeElement) element;
                    final List<TypeElement> filteredListeners = filterListeners(listeners, viewModelType);
                    writeClasses(viewModelType, filteredListeners);
                }
            }
        }

        return false;
    }

    private List<TypeElement> filterListeners(List<TypeElement> listeners, TypeElement type) {
        final TypeMirror viewModelTypeMirror = type.asType();
        final String viewModelClassName = viewModelTypeMirror.toString();

        final List<TypeElement> filteredListeners = new ArrayList<>();
        for (TypeElement listener : listeners) {
            final AnnotationValue viewModelClassValue = Utils.getAnnotationValue(listener, Annotations.LISTENER, "value");
            final String listenerViewModelClassName = viewModelClassValue.getValue().toString();
            if (viewModelClassName.equals(listenerViewModelClassName)) {
                filteredListeners.add(listener);
            }
        }

        return filteredListeners;
    }

    private void writeClasses(TypeElement element, List<TypeElement> listeners) {
        try {
            final AnalyzerResult result = mViewModelAnalyzer.analyze(element, listeners);
            final ViewHolderInfo info = mViewHolderBuilder.build(result);
            mViewHolderFactoryBuilder.build(result, info);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not generate view holder for " + element.getSimpleName(), element);
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotations = new HashSet<>();
        annotations.add(Annotations.LAYOUT);
        annotations.add(Annotations.LISTENER);
        return annotations;
    }
}
