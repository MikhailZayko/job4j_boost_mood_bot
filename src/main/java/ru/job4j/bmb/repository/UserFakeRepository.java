package ru.job4j.bmb.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Profile("test")
@Repository
public class UserFakeRepository implements UserRepository {

    private final Map<Long, User> userMap = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User findByClientId(Long clientId) {
        return userMap.get(clientId);
    }

    public void save(User user) {
        userMap.put(user.getClientId(), user);
    }
}