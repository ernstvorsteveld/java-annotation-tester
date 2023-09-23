package com.sternitc.annotationtester;

import com.sternitc.annotationtester.example.*;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedExampleTest {

    @Test
    public void should_have_type_annotation() {
        AnnotationTester annotationTester = new AnnotationTester(getTests());

        assertThat(annotationTester.validTypeAnnotations()).isTrue();
        assertThat(annotationTester.validMethodAnnotations()).isTrue();
        assertThat(annotationTester.hasTypeAnnotationCount()).isTrue();
        assertThat(annotationTester.hasMethodAnnotationCount()).isTrue();
    }

    AnnotationsSpec getTests() {
        List<AnnotationSpec<?>> annotationsList = new ArrayList<>();
        annotationsList.add(new AnnotationSpec<>(ElementType.TYPE, TypeAnnotation.class, null, "class"));
        annotationsList.add(new AnnotationSpec<>(ElementType.TYPE, TypeAnnotation2.class, null, "class2"));
        annotationsList.add(new AnnotationSpec<>(ElementType.FIELD, FieldAnnotation.class, "attribute", "field"));
        annotationsList.add(new AnnotationSpec<>(ElementType.METHOD, MethodAnnotation.class, "method", "method"));
        return new AnnotationsSpec(AnnotatedExample.class, annotationsList);
    }

}


