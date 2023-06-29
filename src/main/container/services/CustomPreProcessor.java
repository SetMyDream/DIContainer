package main.container.services;

public class CustomPreProcessor implements ComponentPreProcessor {
    @Override
    public void process(Object component) {
        System.out.println("Pre process for " + component.getClass());
    }
}
