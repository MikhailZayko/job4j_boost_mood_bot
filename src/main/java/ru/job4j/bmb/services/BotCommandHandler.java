package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

@Service
public class BotCommandHandler {

    void receive(Content content) {
        System.out.println(content);
    }

    @PostConstruct
    public void init() {
        System.out.println("Bean 'BotCommandHandler' is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 'BotCommandHandler' will be destroyed now.");
    }
}
