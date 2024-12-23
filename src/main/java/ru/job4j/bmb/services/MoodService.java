package ru.job4j.bmb.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.events.UserEvent;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {

    private final ApplicationEventPublisher publisher;

    private final MoodLogRepository moodLogRepository;

    private final MoodRepository moodRepository;

    private final RecommendationEngine recommendationEngine;

    private final UserRepository userRepository;

    private final AchievementRepository achievementRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private static final long WEEK = 604800L;

    private static final long MONTH = 2592000L;

    public MoodService(ApplicationEventPublisher publisher,
                       MoodLogRepository moodLogRepository,
                       MoodRepository moodRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository) {
        this.publisher = publisher;
        this.moodLogRepository = moodLogRepository;
        this.moodRepository = moodRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        Mood mood = moodRepository.findAll().stream()
                .filter(m -> moodId.equals(m.getId()))
                .findFirst().orElse(null);
        moodLogRepository.save(new MoodLog(user, mood, Instant.now().getEpochSecond()));
        publisher.publishEvent(new UserEvent(this, user));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        Content content = new Content(chatId);
        content.setText(formatMoodLogs(getLogsByUserForPeriod(chatId, clientId, WEEK), "Mood logs for the past week"));
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        Content content = new Content(chatId);
        content.setText(formatMoodLogs(getLogsByUserForPeriod(chatId, clientId, MONTH), "Mood logs for the past month"));
        return Optional.of(content);
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        Content content = new Content(chatId);
        User user = userRepository.findAll().stream()
                .filter(u -> clientId.equals(u.getClientId()) && chatId == u.getChatId())
                .findFirst().orElse(null);
        List<Achievement> achievements = achievementRepository.findAll().stream()
                .filter(achievement -> achievement.getUser().equals(user))
                .toList();
        content.setText(formatAchievements(achievements));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    private String formatAchievements(List<Achievement> achievements) {
        if (achievements.isEmpty()) {
            return "You don't have any achievements yet :(";
        }
        var sb = new StringBuilder("Your achievements:\n");
        achievements.forEach(achievement -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(achievement.getCreateAt()));
            sb.append(formattedDate).append(": ").append(achievement.getAward().getTitle()).append("\n");
        });
        return sb.toString();
    }

    private List<MoodLog> getLogsByUserForPeriod(long chatId, Long clientId, long period) {
        User user = userRepository.findAll().stream()
                .filter(u -> clientId.equals(u.getClientId()) && chatId == u.getChatId())
                .findFirst().orElse(null);
        return moodLogRepository.findAll().stream()
                .filter(log -> log.getUser().equals(user) && Instant.now().getEpochSecond() - log.getCreatedAt() < period)
                .toList();
    }
}