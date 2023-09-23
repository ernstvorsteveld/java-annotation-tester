package com.sternitc.annotationtester;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class AnnotationTester {

    private final List<AnnotationSpec<?>> expectedAnnotations;
    private final List<MethodAnnotationsSpec> methodAnnotationsSpecs;
    private final Class<?> clazz;

    public AnnotationTester(AnnotationsSpec annotationsSpec) {
        this.clazz = load(annotationsSpec.classToCheck());
        this.expectedAnnotations = annotationsSpec.annotations();
        this.methodAnnotationsSpecs = getExpectedMethodAnnotations();
    }

    private Class<?> load(Class<?> clazz) {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot load class: " + clazz.getName());
        }
    }

    public boolean validTypeAnnotations() {
        return getExpectedTypeAnnotations()
                .map(s -> clazz.getAnnotation(s.annotation()))
                .filter(Objects::nonNull)
                .count() == getExpectedTypeAnnotations().count();
    }

    private Stream<AnnotationSpec<?>> getExpectedTypeAnnotations() {
        return expectedAnnotations
                .stream()
                .filter(AnnotationSpec::isType);
    }

    public boolean validMethodAnnotations() {
        return getMethodAnnotationSpecs()
                .map(this::getRequiredMethodAnnotation)
                .filter(Objects::nonNull)
                .count() == getMethodAnnotationSpecs().count();
    }

    private Stream<AnnotationSpec<?>> getMethodAnnotationSpecs() {
        return expectedAnnotations.stream()
                .filter(AnnotationSpec::isMethod);
    }

    private List<MethodAnnotationsSpec> getExpectedMethodAnnotations() {
        return getMethodAnnotationSpecs()
                .map(AnnotationSpec::name)
                .distinct()
                .map(m -> new MethodAnnotationsSpec(clazz, m, getMethodSpecs(m)))
                .toList();
    }

    private List<AnnotationSpec<?>> getMethodSpecs(String methodName) {
        return getMethodAnnotationSpecs()
                .filter(s -> s.name().equals(methodName))
                .toList();
    }

    private Annotation getRequiredMethodAnnotation(AnnotationSpec<?> spec) {
        Method toValidate = getMethodToValidate(spec);
        Annotation annotation = toValidate.getAnnotation(spec.annotation());
        if (annotation == null) {
            throw new RuntimeException("Required annotation not found on method: " + spec);
        }
        return toValidate.getAnnotation(spec.annotation());
    }

    private Method getMethodToValidate(AnnotationSpec<?> spec) {
        Method method = getMethod(spec.name());
        if (method == null) {
            throw new RuntimeException("Cannot find required method for spec: " + spec);
        }
        return method;
    }

    private Method getMethod(String methodName) {
        return Arrays.stream(clazz.getMethods()).
                filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow();
    }

    public boolean hasTypeAnnotationCount() {
        return getExpectedTypeAnnotations().count() == getActualTypeAnnotations().count();
    }

    private Stream<Annotation> getActualTypeAnnotations() {
        return Arrays.stream(clazz.getAnnotations());
    }

    public boolean hasMethodAnnotationCount() {
        return methodAnnotationsSpecs
                .stream()
                .map(this::getActualMethodAnnotations)
                .allMatch(ActualAnnotations::isValid);
    }

    private ActualAnnotations getActualMethodAnnotations(MethodAnnotationsSpec methodAnnotationsSpec) {
        return new ActualAnnotations(
                methodAnnotationsSpec.size(),
                getAnnotations(methodAnnotationsSpec.method()).length);
    }

    private Annotation[] getAnnotations(String name) {
        return getMethod(name).getAnnotations();
    }

}

record AnnotationsSpec(
        Class<?> classToCheck,
        List<AnnotationSpec<?>> annotations) {
}

record AnnotationSpec<A extends Annotation>(
        ElementType type,
        Class<A> annotation,
        String name,
        String key) {
    boolean isType() {
        return type().equals(ElementType.TYPE);
    }

    public boolean isMethod() {
        return type().equals(ElementType.METHOD);
    }
}

record MethodAnnotationsSpec(
        Class<?> clazz,
        String method,
        List<AnnotationSpec<?>> annotations) {
    public long size() {
        return annotations().size();
    }
}

record ActualAnnotations(
        long expected,
        long actual) {
    public boolean isValid() {
        return expected == actual;
    }
}