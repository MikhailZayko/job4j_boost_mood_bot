package ru.job4j.bmb.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.events.UserEvent;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private MoodLogRepository moodLogRepository;

    @Mock
    private AwardRepository awardRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private SentContent sentContent;

    @InjectMocks
    private AchievementService achievementService;

    private UserEvent userEvent;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setChatId(1);
        user.setClientId(2);
        userEvent = new UserEvent(this, user);
    }

    @Test
    void when3goodThen3daysAward() {
        List<MoodLog> moodLogs = List.of(
                createMoodLog("Norm", true),
                createMoodLog("Super", true),
                createMoodLog("Happy", true)
        );
        Award award = getAwards().get(2);
        when(moodLogRepository.findByUserId(user.getClientId())).thenReturn(moodLogs);
        when(awardRepository.findAll()).thenReturn(getAwards());
        achievementService.onApplicationEvent(userEvent);
        ArgumentCaptor<Content> contentCaptor = ArgumentCaptor.forClass(Content.class);
        verify(achievementRepository).save(argThat(achievement ->
                achievement.getUser().equals(user)
                        && achievement.getAward().equals(award)
                        && achievement.getCreateAt() > 0
        ));
        verify(sentContent).sent(contentCaptor.capture());
        assertThat(contentCaptor.getValue().getText()).contains(
                "Поздравляем с получением достижения",
                "Молодец",
                "За 3 дня"
        );
    }

    @Test
    void when2good1bad2goodThen2daysAward() {
        List<MoodLog> moodLogs = List.of(
                createMoodLog("Norm", true),
                createMoodLog("Super", true),
                createMoodLog("Sad", false),
                createMoodLog("Happy", true),
                createMoodLog("Ok", true)
        );
        Award award = getAwards().get(1);
        when(moodLogRepository.findByUserId(user.getClientId())).thenReturn(moodLogs);
        when(awardRepository.findAll()).thenReturn(getAwards());
        achievementService.onApplicationEvent(userEvent);
        ArgumentCaptor<Content> contentCaptor = ArgumentCaptor.forClass(Content.class);
        verify(achievementRepository).save(argThat(achievement ->
                achievement.getUser().equals(user)
                        && achievement.getAward().equals(award)
                        && achievement.getCreateAt() > 0
        ));
        verify(sentContent).sent(contentCaptor.capture());
        assertThat(contentCaptor.getValue().getText()).contains(
                "Поздравляем с получением достижения",
                "Почти молодец",
                "За 2 дня"
        );
    }

    @Test
    void when2good2badThenNoAward() {
        List<MoodLog> moodLogs = List.of(
                createMoodLog("Norm", true),
                createMoodLog("Super", true),
                createMoodLog("Sad", false),
                createMoodLog("So sad", false)
        );
        when(moodLogRepository.findByUserId(user.getClientId())).thenReturn(moodLogs);
        when(awardRepository.findAll()).thenReturn(getAwards());
        achievementService.onApplicationEvent(userEvent);
        verify(achievementRepository, never()).save(any(Achievement.class));
        verify(sentContent, never()).sent(any(Content.class));
    }

    private MoodLog createMoodLog(String moodName, boolean isGood) {
        return new MoodLog(user, new Mood(moodName, isGood), Instant.now().getEpochSecond());
    }

    private List<Award> getAwards() {
        return List.of(
                new Award("Почти почти молодец", "За 1 день", 1),
                new Award("Почти молодец", "За 2 дня", 2),
                new Award("Молодец", "За 3 дня", 3)
        );
    }

}