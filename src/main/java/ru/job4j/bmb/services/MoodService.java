package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class MoodService implements BeanNameAware {

    @PostConstruct
    public void init() {
        System.out.println("Bean 'MoodService' is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 'MoodService' will be destroyed now.");
    }

    @Override
    public void setBeanName(@NonNull String name) {
        System.out.println(name);
    }
}
