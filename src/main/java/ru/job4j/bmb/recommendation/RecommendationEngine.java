package ru.job4j.bmb.recommendation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class RecommendationEngine {

    @PostConstruct
    public void init() {
        System.out.println("Bean 'RecommendationEngine' is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 'RecommendationEngine' will be destroyed now.");
    }
}
