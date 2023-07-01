package main.container.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationProcessor {
    private final Object instance;
    private final Method method;

    public AnnotationProcessor(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public void process() {
        try {
            method.setAccessible(true);
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}


