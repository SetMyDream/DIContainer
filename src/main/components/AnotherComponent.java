package main.components;


import main.container.annotations.Component;
import main.container.annotations.PostConstructor;
import main.container.annotations.PostProcessor;
import main.container.annotations.PreProcessor;

@Component
public class AnotherComponent {
    @PostConstructor
    public void init() {
        System.out.println("AnotherComponent initialized");
    }


//    @PreProcessor
    public void preProcess() {
        System.out.println("Pre-processing " + getClass());
    }

    @PostProcessor
    public void postProcess() {
        System.out.println("Post-processing " + getClass());
    }
}