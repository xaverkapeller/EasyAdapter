package com.github.easyadapter.processor;

import com.github.easyadapter.processor.builder.AnnotationViewHolderBuilder;
import com.github.easyadapter.codewriter.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public class EasyAdapterProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

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
            final List<TypeElement> listeners = listenerAnnotation != null
                    ? roundEnv.getElementsAnnotatedWith(listenerAnnotation).stream()
                        .filter(TypeElement.class::isInstance)
                        .map(TypeElement.class::cast)
                        .collect(Collectors.toList())
                    : new ArrayList<>();

            roundEnv.getElementsAnnotatedWith(layoutAnnotation).stream()
                    .filter(TypeElement.class::isInstance)
                    .map(TypeElement.class::cast)
                    .forEach(viewmodel -> buildClasses(viewmodel, listeners));
        }

        return false;
    }

    private void buildClasses(TypeElement element, List<TypeElement> listeners) {
        try {
            final AnnotationViewHolderBuilder builder = new AnnotationViewHolderBuilder(processingEnv, element, listeners);
            builder.build();
        } catch (Exception e) {
            Utils.error(processingEnv, "Could not generate view holder for an Element!", element);
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
