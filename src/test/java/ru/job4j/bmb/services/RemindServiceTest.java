package ru.job4j.bmb.services;

import org.junit.jupiter.api.Test;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodFakeRepository;
import ru.job4j.bmb.repository.MoodLogFakeRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.MoodRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RemindServiceTest {

    @Test
    public void whenMoodGood() {
        List<Content> result = new ArrayList<>();
        SentContent sentContent = result::add;
        MoodRepository moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        MoodLogRepository moodLogRepository = new MoodLogFakeRepository();
        User user = new User();
        user.setChatId(100);
        MoodLog moodLog = new MoodLog();
        moodLog.setUser(user);
        long tenDaysAgo = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(tenDaysAgo);
        moodLogRepository.save(moodLog);
        TgUI tgUI = new TgUI(moodRepository);
        new RemindService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }

    @Test
    void whenUserVotedToday() {
        List<Content> result = new ArrayList<>();
        SentContent sentContent = result::add;
        MoodRepository moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        MoodLogRepository moodLogRepository = new MoodLogFakeRepository();
        User user = new User();
        user.setChatId(100);
        MoodLog moodLog = new MoodLog();
        moodLog.setUser(user);
        long today = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() + 10000;
        moodLog.setCreatedAt(today);
        moodLogRepository.save(moodLog);
        TgUI tgUI = new TgUI(moodRepository);
        new RemindService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result).isEmpty();
    }
}