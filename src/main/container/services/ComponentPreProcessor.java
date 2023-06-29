package main.container.services;

//Возможность добавлять обработчики ПЕРЕД добавлением в контейнер
public interface ComponentPreProcessor {
    void process(Object component);
}
