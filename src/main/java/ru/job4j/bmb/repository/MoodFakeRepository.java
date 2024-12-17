package ru.job4j.bmb.repository;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.Mood;

import java.util.ArrayList;
import java.util.List;

public class MoodFakeRepository
        extends CrudRepositoryFake<Mood, Long>
        implements MoodRepository {

    public List<Mood> findAll() {
        return new ArrayList<>(memory.values());
    }
}