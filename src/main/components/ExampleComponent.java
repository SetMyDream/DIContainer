package main.components;


import main.annotations.Autowired;
import main.annotations.Component;

@Component
public
class ExampleComponent {
    private final AnotherComponent anotherComponent;

    @Autowired
    public ExampleComponent(AnotherComponent anotherComponent) {
        this.anotherComponent = anotherComponent;
    }

    public void doSomething() {
        System.out.println("Doing something with AnotherComponent: " + anotherComponent);
    }
}

