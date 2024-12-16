package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.exceptions.CheckedConsumer;
import ru.job4j.bmb.exceptions.SentContentException;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {

    private final BotCommandHandler handler;

    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        if (content.getAudio() != null) {
            sendWithExceptionHandling(new SendAudio(), sendAudio -> {
                sendAudio.setChatId(content.getChatId());
                sendAudio.setAudio(content.getAudio());
                if (content.getText() != null) {
                    sendAudio.setCaption(content.getText());
                }
                execute(sendAudio);
            });
        } else if (content.getPhoto() != null) {
            sendWithExceptionHandling(new SendPhoto(), sendPhoto -> {
                sendPhoto.setChatId(content.getChatId());
                sendPhoto.setPhoto(content.getPhoto());
                if (content.getText() != null) {
                    sendPhoto.setCaption(content.getText());
                }
                execute(sendPhoto);
            });
        } else {
            sendWithExceptionHandling(new SendMessage(), sendMessage -> {
                sendMessage.setChatId(content.getChatId());
                if (content.getMarkup() != null) {
                    sendMessage.setReplyMarkup(content.getMarkup());
                }
                if (content.getText() != null) {
                    sendMessage.setText(content.getText());
                }
                execute(sendMessage);
            });
        }
    }

    private <T> void sendWithExceptionHandling(T method, CheckedConsumer<T> configurator) {
        try {
            configurator.accept(method);
        } catch (TelegramApiException e) {
            throw new SentContentException(e.getMessage(), e);
        }
    }
}