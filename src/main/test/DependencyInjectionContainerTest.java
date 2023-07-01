package main.test;

import main.components.AnotherComponent;
import main.container.DependencyInjectionContainer;
import main.components.ExampleComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertNotNull;

public class DependencyInjectionContainerTest {

    private DependencyInjectionContainer container;

    @BeforeEach
    public void setUp() {
        container = new DependencyInjectionContainer();
    }

    //Контейнер должен быть изолированным.
    @Test
    public void testContainerIsolation() throws Exception {
        DependencyInjectionContainer container1 = new DependencyInjectionContainer();
        container1.scanAndRegisterComponents("main.components");

        DependencyInjectionContainer container2 = new DependencyInjectionContainer();
        container2.scanAndRegisterComponents("main.components");

        ExampleComponent exampleComponent1 = container1.getInstance(ExampleComponent.class);
        ExampleComponent exampleComponent2 = container2.getInstance(ExampleComponent.class);

        assertNotSame(exampleComponent1, exampleComponent2);
    }

    @Test
    public void testExampleComponent() {
        try {
            container.register(ExampleComponent.class);
            container.register(AnotherComponent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ExampleComponent exampleComponent = container.getInstance(ExampleComponent.class);
        assertNotNull(exampleComponent);

        exampleComponent.doSomething();
    }

    @Test
    public void testAnotherComponent() {
        try {
            container.register(AnotherComponent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        AnotherComponent anotherComponent = container.getInstance(AnotherComponent.class);
        assertNotNull(anotherComponent);
    }
}
