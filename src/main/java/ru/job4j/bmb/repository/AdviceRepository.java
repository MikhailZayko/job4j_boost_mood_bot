package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.Advice;

import java.util.List;

@Repository
public interface AdviceRepository extends CrudRepository<Advice, Long> {

    List<Advice> findAll();
}
