package ru.job4j.bmb.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "mb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", unique = true)
    private long clientId;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "advices_enabled")
    private boolean advicesEnabled = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public boolean isAdvicesEnabled() {
        return advicesEnabled;
    }

    public void setAdvicesEnabled(boolean advicesEnabled) {
        this.advicesEnabled = advicesEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}