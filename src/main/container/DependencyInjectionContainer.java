package main.container;

import main.container.services.ClassScanner;
import main.container.annotations.Autowired;
import main.container.annotations.Component;
import main.container.annotations.PostConstructor;
import main.container.annotations.Qualifier;
import main.container.services.ComponentPreProcessor;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;


public class DependencyInjectionContainer {
    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();
    private final List<ComponentPreProcessor> preProcessors = new ArrayList<>();


    public void scanAndRegisterComponents(String basePackage) throws Exception {
        ClassScanner classScanner = new ClassScanner();
        List<Class<?>> componentClasses = classScanner.scanClasses(basePackage);
        registerComponentClasses(componentClasses);
    }

    private void registerComponentClasses(List<Class<?>> componentClasses) throws Exception {
        for (Class<?> componentClass : componentClasses) {
            register(componentClass);
        }
    }

    public <T> void register(Class<T> componentClass) throws Exception {
        //Will skip not components for now instead of exception;
        if (!componentClass.isAnnotationPresent(Component.class)) {
            return;
//            throw new IllegalArgumentException("Class must be annotated with @Component: " + componentClass);
        }

        T instance = createInstance(componentClass);
        preProcess(instance);
        postProcess(instance);

        instances.put(componentClass, instance);
    }

    //Доступ к инстансам должен предоставляться методом контейнера, по классу.
    public <T> T getInstance(Class<T> componentClass) {
        return componentClass.cast(instances.get(componentClass));
    }

    public void initialize() throws Exception {
        for (Object instance : instances.values()) {
            invokePostConstructors(instance);
        }
    }

    private <T> T createInstance(Class<T> componentClass) throws Exception {
        Constructor<?>[] constructors = componentClass.getDeclaredConstructors();
        Constructor<?> constructor = findAutowiredConstructor(constructors);

        if (constructor == null) {
            constructor = constructors[0];
        }

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        // Зависимости компонента должны быть автоматически включены в него.
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] annotations = constructor.getParameterAnnotations()[i];
            Object dependency = findDependency(parameterType, annotations);
            parameters[i] = dependency;
        }

        constructor.setAccessible(true);
        return componentClass.cast(constructor.newInstance(parameters));
    }

    private Constructor<?> findAutowiredConstructor(Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
        }
        return null;
    }

    private Object findDependency(Class<?> parameterType, Annotation[] annotations) {
        if (annotations.length == 0) {
            return getInstance(parameterType);
        }

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Qualifier.class)) {
                Qualifier qualifier = (Qualifier) annotation;
                String value = qualifier.value();
                try {
                    Class<?> dependencyClass = Class.forName(value);
                    return getInstance(dependencyClass);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Invalid dependency class: " + value, e);
                }
            }
        }

        throw new IllegalArgumentException("No matching dependency found for parameter type: " + parameterType);
    }

    private void invokePostConstructors(Object instance) throws Exception {
        Method[] methods = instance.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(PostConstructor.class)) {
                method.setAccessible(true);
                method.invoke(instance);
            }
        }
    }

    //Возможность добавлять обработчики ПЕРЕД добавлением в контейнер
    private <T> void preProcess(T component) {
        for (ComponentPreProcessor preProcessor : preProcessors) {
            preProcessor.process(component);
        }
    }

    //Добавить препроцессор к кмопоненту
    public void addComponentPreProcessor(ComponentPreProcessor preProcessor) {
        preProcessors.add(preProcessor);
    }

    //Возможность добавлять обработчики ПОСЛЕ добавления в контейнер
    private void postProcess(Object instance) {
        System.out.println("Post process for " + instance.getClass());
        // Add other behavior later, if necessary
    }
}



