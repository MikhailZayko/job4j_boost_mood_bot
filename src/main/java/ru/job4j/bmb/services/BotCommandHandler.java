package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Optional;

@Service
public class BotCommandHandler {

    private final UserRepository userRepository;

    private final MoodService moodService;

    private final AdviceService adviceService;

    private final TgUI tgUI;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             AdviceService adviceService,
                             TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.adviceService = adviceService;
        this.tgUI = tgUI;
    }

    Optional<Content> commands(Message message) {
        long chatId = message.getChatId();
        Long clientId = message.getFrom().getId();
        return switch (message.getText()) {
            case "/start" -> handleStartCommand(chatId, clientId);
            case "/week_mood_log" -> moodService.weekMoodLogCommand(chatId, clientId);
            case "/month_mood_log" -> moodService.monthMoodLogCommand(chatId, clientId);
            case "/award" -> moodService.awards(chatId, clientId);
            case "/daily_advice" -> adviceService.offerAdvice(chatId, clientId);
            case "/enable_advices" -> toggleReminders(chatId, clientId, true);
            case "/disable_advices" -> toggleReminders(chatId, clientId, false);
            default -> Optional.empty();
        };
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        Long moodId = Long.valueOf(callback.getData());
        Optional<User> user = userRepository.findById(callback.getFrom().getId());
        return user.map(value -> moodService.chooseMood(value, moodId));
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        User user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        userRepository.save(user);
        Content content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }

    private Optional<Content> toggleReminders(long chatId, Long clientId, boolean enable) {
        User user = userRepository.findByClientId(clientId);
        user.setAdvicesEnabled(enable);
        userRepository.save(user);
        String status = enable ? "включены" : "отключены";
        Content content = new Content(chatId);
        content.setText("Советы дня " + status);
        return Optional.of(content);
    }
}