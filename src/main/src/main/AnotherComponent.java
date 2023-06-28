package main.src.main;


import main.src.main.annotations.Component;
import main.src.main.annotations.PostConstructor;

@Component
public
class AnotherComponent {
    @PostConstructor
    public void init() {
        System.out.println("AnotherComponent initialized");
    }
}