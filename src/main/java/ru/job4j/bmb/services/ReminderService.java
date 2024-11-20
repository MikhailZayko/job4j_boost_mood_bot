package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {

    @PostConstruct
    public void init() {
        System.out.println("Bean 'ReminderService' is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 'ReminderService' will be destroyed now.");
    }
}
