package main.components;


import main.container.annotations.Autowired;
import main.container.annotations.Component;
import main.container.annotations.PostProcessor;
import main.container.annotations.PreProcessor;

@Component
public class ExampleComponent {
//    private final AnotherComponent anotherComponent;

    @PreProcessor
    public void preProcess() {
        System.out.println("Pre-processing ExampleComponent");
    }

//    @PostProcessor
    public void postProcess() {
        System.out.println("Post-processing ExampleComponent");
    }

    @Autowired
    public ExampleComponent(AnotherComponent anotherComponent) {
//        this.anotherComponent = anotherComponent;
    }

    public void doSomething() {
//        System.out.println("Doing something with AnotherComponent: " + anotherComponent);
    }
}

