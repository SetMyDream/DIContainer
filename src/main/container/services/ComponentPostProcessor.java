package main.container.services;

import java.lang.reflect.Method;

//Возможность добавлять обработчики ПЕРЕД добавлением в контейнер
public class ComponentPostProcessor extends AnnotationProcessor {
    public ComponentPostProcessor(Object instance, Method method) {
        super(instance, method);
    }

    public void process(Object component) {

    }
}
