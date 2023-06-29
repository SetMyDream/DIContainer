package main.components;


import main.container.annotations.Component;
import main.container.annotations.PostConstructor;

@Component
public
class AnotherComponent {
    @PostConstructor
    public void init() {
        System.out.println("AnotherComponent initialized");
    }
}