package ru.job4j.bmb.exceptions;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t) throws TelegramApiException;
}
