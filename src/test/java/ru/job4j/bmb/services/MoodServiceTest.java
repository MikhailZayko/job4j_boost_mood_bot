package ru.job4j.bmb.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoodServiceTest {

    @Mock
    private MoodLogRepository moodLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MoodService moodService;

    private User user;

    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setChatId(1);
        user.setClientId(2);
        formatter = DateTimeFormatter
                .ofPattern("dd-MM-yyyy HH:mm")
                .withZone(ZoneId.systemDefault());
    }

    @Test
    void whenNoMoodLogsThenReturnMessageNoLogsFound() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(moodLogRepository.findAll()).thenReturn(List.of());
        Content content = moodService.weekMoodLogCommand(
                user.getChatId(),
                user.getClientId()
        ).orElse(null);
        assertThat(content.getText()).isEqualTo("Mood logs for the past week:\nNo mood logs found.");
    }

    @Test
    void whenMoodLogsExistThenReturnFormattedLogs() {
        MoodLog log = new MoodLog(
                user,
                new Mood("Norm", true),
                Instant.now().getEpochSecond()
        );
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(moodLogRepository.findAll()).thenReturn(List.of(log));
        Content content = moodService.weekMoodLogCommand(
                user.getChatId(),
                user.getClientId()
        ).orElse(null);
        String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
        String expectedText = "Mood logs for the past week:\n" + formattedDate + ": Norm\n";
        assertThat(content.getText()).isEqualTo(expectedText);
    }
}