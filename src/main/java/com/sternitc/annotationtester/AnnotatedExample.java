package com.sternitc.annotationtester;

@TypeAnnotation(key = "class")
@TypeAnnotation2(key = "class2")
public class AnnotatedExample {

    @FieldAnnotation(key = "attribute")
    private Object attribute;

    @MethodAnnotation(key = "method")
    public void method() {
    }
}
