package main.container.services;

import java.lang.reflect.Method;

public class CustomPreProcessor extends ComponentPreProcessor {
    public CustomPreProcessor(Object instance, Method method) {
        super(instance, method);
    }

    public void process(Object component) {
        System.out.println("Custom pre process for " + component.getClass());
    }
}
