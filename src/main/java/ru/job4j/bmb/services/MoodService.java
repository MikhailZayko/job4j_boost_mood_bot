package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class MoodService {

    @PostConstruct
    public void init() {
        System.out.println("Bean 'MoodService' is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 'MoodService' will be destroyed now.");
    }
}
