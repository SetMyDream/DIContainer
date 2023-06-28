package main.components;


import main.annotations.Component;
import main.annotations.PostConstructor;

@Component
public
class AnotherComponent {
    @PostConstructor
    public void init() {
        System.out.println("AnotherComponent initialized");
    }
}