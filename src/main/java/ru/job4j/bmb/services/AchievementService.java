package ru.job4j.bmb.services;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.events.UserEvent;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.time.Instant;
import java.util.Optional;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {

    private final MoodLogRepository moodLogRepository;

    private final AwardRepository awardRepository;

    private final AchievementRepository achievementRepository;

    private final SentContent sentContent;

    public AchievementService(MoodLogRepository moodLogRepository,
                              AwardRepository awardRepository,
                              AchievementRepository achievementRepository,
                              SentContent sentContent) {
        this.moodLogRepository = moodLogRepository;
        this.awardRepository = awardRepository;
        this.achievementRepository = achievementRepository;
        this.sentContent = sentContent;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        User user = event.getUser();
        long goodMoodDays = moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == user.getClientId()
                        && moodLog.getMood().isGood())
                .count();
        Optional<Award> optionalAward = awardRepository.findAll().stream()
                .filter(award -> award.getDays() == goodMoodDays)
                .findFirst();
        optionalAward.ifPresent(award -> {
            achievementRepository.save(new Achievement(Instant.now().getEpochSecond(), user, award));
            Content content = new Content(user.getChatId());
            content.setText("Поздравляем с получением достижения: " + award.getTitle() + System.lineSeparator()
                    + award.getDescription());
            sentContent.sent(content);
        });
    }
}