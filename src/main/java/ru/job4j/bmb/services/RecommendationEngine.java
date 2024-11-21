package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.content.ContentProvider;

import java.util.List;
import java.util.Random;

@Service
public class RecommendationEngine implements BeanNameAware {

    private final List<ContentProvider> contents;

    private static final Random RND = new Random(System.currentTimeMillis());

    public RecommendationEngine(List<ContentProvider> contents) {
        this.contents = contents;
    }

    public Content recommendFor(Long chatId, Long moodId) {
        int index = RND.nextInt(0, contents.size());
        return contents.get(index).byMood(chatId, moodId);
    }

    @PostConstruct
    public void init() {
        System.out.println("Bean 'RecommendationEngine' is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 'RecommendationEngine' will be destroyed now.");
    }

    @Override
    public void setBeanName(@NonNull String name) {
        System.out.println(name);
    }
}