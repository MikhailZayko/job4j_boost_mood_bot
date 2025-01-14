package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Advice;
import ru.job4j.bmb.repository.AdviceRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AdviceService {

    private final MoodLogRepository moodLogRepository;

    private final AdviceRepository adviceRepository;

    private static final Random RND = new Random();

    public AdviceService(MoodLogRepository moodLogRepository,
                         AdviceRepository adviceRepository) {
        this.moodLogRepository = moodLogRepository;
        this.adviceRepository = adviceRepository;
    }

    public Optional<Content> offerAdvice(long chatId, Long clientId) {
        Content content = new Content(chatId);
        moodLogRepository.findByUserIdOrderByCreatedAtDesc(clientId)
                .findFirst()
                .ifPresent(moodLog -> {
                    if (moodLog.getMood().isGood()) {
                        content.setText(goodMoodAdvice().getText());
                    } else {
                        content.setText(badMoodAdvice().getText());
                    }
                });
        return Optional.of(content);
    }

    private Advice goodMoodAdvice() {
        List<Advice> goodMoodAdvices = adviceRepository.findAll().stream()
                .filter(Advice::isGoodMood)
                .toList();
        return goodMoodAdvices.get(RND.nextInt(goodMoodAdvices.size()));
    }

    private Advice badMoodAdvice() {
        List<Advice> badMoodAdvices = adviceRepository.findAll().stream()
                .filter(advice -> !advice.isGoodMood())
                .toList();
        return badMoodAdvices.get(RND.nextInt(badMoodAdvices.size()));
    }
}