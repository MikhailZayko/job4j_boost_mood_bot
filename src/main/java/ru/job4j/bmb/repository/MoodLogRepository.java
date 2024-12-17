package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {

    List<MoodLog> findAll();

    default List<MoodLog> findByUserId(Long userId) {
        return findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == userId)
                .collect(Collectors.toList());
    }

    default Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == userId)
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed());
    }

    default List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay) {
        List<User> allUsers = findAll().stream()
                .map(MoodLog::getUser)
                .distinct()
                .collect(Collectors.toList());
        List<User> todayUsers = findAll().stream()
                .filter(moodLog -> moodLog.getCreatedAt() >= startOfDay)
                .filter(moodLog -> moodLog.getCreatedAt() <= endOfDay)
                .map(MoodLog::getUser)
                .distinct()
                .toList();
        allUsers.removeAll(todayUsers);
        return allUsers;
    }

    default List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart) {
        return findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == userId)
                .filter(moodLog -> moodLog.getCreatedAt() >= weekStart)
                .collect(Collectors.toList());
    }

    default List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart) {
        return findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == userId)
                .filter(moodLog -> moodLog.getCreatedAt() >= monthStart)
                .collect(Collectors.toList());
    }
}