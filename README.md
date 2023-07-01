# Dependency Injection Container

The Dependency Injection Container is a custom container for Inversion of Control (IoC) that uses Dependency Injection through `java.lang.reflect`. It provides a way to manage component dependencies and perform dependency injection automatically.

## Workflow

1. Create a component class and annotate it with the `@Component` annotation.
2. Annotate the constructor with `@Autowired` if you use other components as fields in your class.
3. If you have multiple realizations for a common superclass, use `@Qualifier({@code realizationClassName})` to specify the class you need.
4. Use `@PostConstruct` annotation on a method if you want to call it immediately after instance creation.
5. Use `@PreProcessor` on a method if you need to perform some operations with the instance before adding it to the container.
6. Use `@PostProcessor` on a method if you need to perform some operations with the instance immediately after adding it to the container.
7. After creating all components, you can create a new container with the constructor `container = new DependencyInjectionContainer()` and use the method `container.scanAndRegisterComponents(String relativePathScanForComponents)` to scan for and register components automatically based on a base package.
8. Alternatively, you can manually register a component using the `container.register(Class)` method.
9. Use `container.getInstance(ExampleComponent.class)` to retrieve an instance of a component from the container.

## Getting Started

To get started with the Dependency Injection Container, follow these steps:

1. Clone the repository: `git clone https://github.com/SetMyDream/DIContainer.git`
2. Open the project in your preferred Java IDE.
3. Build the project to compile the source code.
4. Import the necessary dependencies if required.
5. Start using the Dependency Injection Container in your own project.

## Usage Examples

### Creating components

#### ExampleComponent
```java
@Component
public class ExampleComponent {
    private final AnotherComponent anotherComponent;

    @PreProcessor
    public void preProcess() {
        System.out.println("Pre-processing class " + getClass());
    }

    @PostProcessor
    public void postProcess() {
        System.out.println("Post-processing ExampleComponent");
    }

    @Autowired
    public ExampleComponent(AnotherComponent anotherComponent) {
        this.anotherComponent = anotherComponent;
    }

    public void doSomething() {
        System.out.println("Doing something with AnotherComponent: " + anotherComponent);
    }
}
```

#### AnotherComponent
```java
@Component
public class AnotherComponent {
    @PostConstructor
    public void init() {
        System.out.println("AnotherComponent initialized");
    }


    @PreProcessor
    public void preProcess() {
        System.out.println("Pre-processing " + getClass());
    }

    @PostProcessor
    public void postProcess() {
        System.out.println("Post-processing " + getClass());
    }
}
}
```
### Scanning, Registering and use Components

```java
public class App {
    public static void main(String[] args) throws Exception {
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        container.scanAndRegisterComponents("main.components");

        System.out.println(container.getInstanceClassNames());

        ExampleComponent exampleComponent = container.getInstance(ExampleComponent.class);
        exampleComponent.doSomething();
    }
}
```

