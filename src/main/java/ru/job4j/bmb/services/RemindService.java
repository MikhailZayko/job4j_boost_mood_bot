package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class RemindService {

    private final SentContent sentContent;

    private final MoodLogRepository moodLogRepository;

    private final TgUI tgUI;

    public RemindService(SentContent sentContent,
                           MoodLogRepository moodLogRepository, TgUI tgUI) {
        this.sentContent = sentContent;
        this.moodLogRepository = moodLogRepository;
        this.tgUI = tgUI;
    }

    @Scheduled(fixedRateString = "${recommendation.alert.period}")
    public void remindUsers() {
        long startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        for (User user : moodLogRepository.findUsersWhoDidNotVoteToday(startOfDay, endOfDay)) {
            Content content = new Content(user.getChatId());
            content.setText("Как настроение?");
            content.setMarkup(tgUI.buildButtons());
            sentContent.sent(content);
        }
    }
}