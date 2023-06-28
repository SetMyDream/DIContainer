package main.src.main;


public class App {
    public static void main(String[] args) throws Exception {
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        container.register(ExampleComponent.class);
        container.register(AnotherComponent.class);
        container.initialize();

        ExampleComponent exampleComponent = container.getInstance(ExampleComponent.class);
        exampleComponent.doSomething();
    }
}
