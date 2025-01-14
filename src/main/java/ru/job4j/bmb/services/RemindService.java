package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class RemindService {

    private final SentContent sentContent;

    private final MoodLogRepository moodLogRepository;

    private final TgUI tgUI;

    private final AdviceService adviceService;

    private final UserRepository userRepository;

    public RemindService(SentContent sentContent,
                         MoodLogRepository moodLogRepository, TgUI tgUI,
                         AdviceService adviceService, UserRepository userRepository) {
        this.sentContent = sentContent;
        this.moodLogRepository = moodLogRepository;
        this.tgUI = tgUI;
        this.adviceService = adviceService;
        this.userRepository = userRepository;
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

    @Scheduled(cron = "${advice.period}")
    public void remindAdvice() {
        userRepository.findAll().stream()
                .filter(User::isAdvicesEnabled)
                .forEach(user -> sentContent.sent(
                        adviceService.offerAdvice(user.getChatId(), user.getClientId()).get()));
    }
}