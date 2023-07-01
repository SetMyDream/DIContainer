package main;

import main.components.ExampleComponent;
import main.container.DependencyInjectionContainer;

public class App {
    public static void main(String[] args) throws Exception {
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        container.scanAndRegisterComponents("main.components");

        //Доступ к инстансам должен предоставляться методом контейнера, по классу.
        ExampleComponent exampleComponent = container.getInstance(ExampleComponent.class);
        System.out.println(container.getInstanceClassNames());
        exampleComponent.doSomething();
    }
}
