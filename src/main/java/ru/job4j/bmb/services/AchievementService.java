package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AchievementService implements BeanNameAware {

    @PostConstruct
    public void init() {
        System.out.println("Bean 'AchievementService' is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 'AchievementService' will be destroyed now.");
    }

    @Override
    public void setBeanName(@NonNull String name) {
        System.out.println(name);
    }
}
