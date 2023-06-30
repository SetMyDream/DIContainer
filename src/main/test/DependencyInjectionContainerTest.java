package main.test;

import main.container.DependencyInjectionContainer;
import main.components.ExampleComponent;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertNotSame;

public class DependencyInjectionContainerTest {


    //Контейнер должен быть изолированным.
    @Test
    public void testContainerIsolation() throws Exception {
        DependencyInjectionContainer container1 = new DependencyInjectionContainer();
        container1.scanAndRegisterComponents("main.components");
        container1.initialize();

        DependencyInjectionContainer container2 = new DependencyInjectionContainer();
        container2.scanAndRegisterComponents("main.components");
        container2.initialize();

        ExampleComponent exampleComponent1 = container1.getInstance(ExampleComponent.class);
        ExampleComponent exampleComponent2 = container2.getInstance(ExampleComponent.class);

        assertNotSame(exampleComponent1, exampleComponent2);
    }
}
