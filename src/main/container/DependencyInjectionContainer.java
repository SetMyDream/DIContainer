package main.container;

import main.container.services.AnnotationProcessor;
import main.container.services.ClassScanner;
import main.container.annotations.Autowired;
import main.container.annotations.Component;
import main.container.annotations.PostConstructor;
import main.container.annotations.Qualifier;
import main.container.annotations.PreProcessor;
import main.container.annotations.PostProcessor;
import main.container.services.ComponentPostProcessor;
import main.container.services.ComponentPreProcessor;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;


/**
 * Custom container for Inversion of Control (IoC) uses Dependency injection through java.lang.reflect
 * <p>Workflow:
 * <p>• Create component class, annotate it with @Component annotation /n
 * <p>• Annotate constructor with @Autowired, if you use other components as fields in your class
 * <p>• If you have multiple realizations for a common superclass, use @Qualifier({@code realizationClassName}) to specify class you need
 * <p>• Use @PostConstruct annotation on your component method if you want to call it immediately after instance creation
 * <p>• Use @PreProcessor on method if you need do something with instance before adding it to container
 * <p>• Use @PostProcessor on method if you need do something with instance immediately after adding it to container
 * <p>• After creating all components you can create new container with constructor {@code container = new DependencyInjectionContainer()}
 * and use method {@code container.scanAndRegisterComponents(String relativePathScanForComponents)}
 * <p>• You can also use method {@code container.register(Class)} to add component to container manually
 * <p>• Use {@code container.getInstance(ExampleComponent.class)} to get instance from container
 */
public class DependencyInjectionContainer {
    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    //Annotation used to scan for components and fill container with it

    /**
     * Scans the specified base package for component classes and registers them in the container.
     *
     * @param basePackage the base package to scan for component classes
     * @throws Exception if an error occurs during the scanning or registration process
     */
    public void scanAndRegisterComponents(String basePackage) throws Exception {
        ClassScanner classScanner = new ClassScanner();
        List<Class<?>> componentClasses = classScanner.scanClasses(basePackage);
        registerComponentClasses(componentClasses);
    }


    /**
     * Registers the specified component class in the container.
     *
     * @param componentClass the component class to register
     * @param <T>            the type of the component class
     * @throws Exception if an error occurs during the registration process
     */
    public <T> void register(Class<T> componentClass) throws Exception {
        //Check if annotation @Component present
        if (!componentClass.isAnnotationPresent(Component.class)) {
            return;
        }

        //Need to initialize some instances once, if they autowired
        if (instances.containsKey(componentClass)) {
            return;
        }

        List<AnnotationProcessor> preProcessors = new ArrayList<>();
        List<AnnotationProcessor> postProcessors = new ArrayList<>();

        T instance = createInstance(componentClass);
        invokePostConstructors(instance);

        Method[] methods = componentClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(PreProcessor.class)) {
                preProcessors.add(new AnnotationProcessor(instance, method));
            } else if (method.isAnnotationPresent(PostProcessor.class)) {
                postProcessors.add(new AnnotationProcessor(instance, method));
            }
        }

        annotationProcess(preProcessors);
        instances.put(componentClass, instance);
        annotationProcess(postProcessors);
    }

    /**
     * Returns a list of class names for all instances currently registered in the container.
     *
     * @return a list of class names for all instances in the container
     */
    public List<String> getInstanceClassNames() {
        List<String> classNames = new ArrayList<>();
        for (Class<?> componentClass : instances.keySet()) {
            classNames.add(componentClass.getName());
        }
        return classNames;
    }


    /**
     * Retrieves an instance of the specified component class from the container.
     *
     * @param componentClass the component class to retrieve an instance of
     * @param <T>            the type of the component class
     * @return an instance of the specified component class, or null if not found
     */
    public <T> T getInstance(Class<T> componentClass) {
        return componentClass.cast(instances.get(componentClass));
    }

    private void registerComponentClasses(List<Class<?>> componentClasses) throws Exception {
        for (Class<?> componentClass : componentClasses) {
            register(componentClass);
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

    private void annotationProcess(List<AnnotationProcessor> preProcessors) {
        for (AnnotationProcessor preProcessor : preProcessors) {
            invokeProcessor(preProcessor);
        }
    }

    //can be further modified, but as for now method excessive
    private void invokeProcessor(AnnotationProcessor processor) {
        processor.process();
    }

    private AnnotationProcessor createAnnotationProcessor(Object processor, Method method) {
        if (processor instanceof ComponentPostProcessor) {
            return new ComponentPostProcessor((ComponentPostProcessor) processor, method);
        } else if (processor instanceof ComponentPreProcessor) {
            return new ComponentPreProcessor((ComponentPreProcessor) processor, method);
        } else {
            return new AnnotationProcessor(processor, method);
        }
    }
}

