package main;


import main.components.AnotherComponent;
import main.components.ExampleComponent;

public class App {
    public static void main(String[] args) throws Exception {
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        container.scanAndRegisterComponents("main.components");
        container.initialize();

        ExampleComponent exampleComponent = container.getInstance(ExampleComponent.class);

        exampleComponent.doSomething();
    }
}
