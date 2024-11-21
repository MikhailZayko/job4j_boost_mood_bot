package ru.job4j.bmb.model;

public class User {

    private Long id;

    private long clientId;

    private long chatId;

    public User(Long id, long clientId, long chatId) {
        this.id = id;
        this.clientId = clientId;
        this.chatId = chatId;
    }

    public User() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return clientId == user.clientId && chatId == user.chatId && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + Long.hashCode(clientId);
        result = 31 * result + Long.hashCode(chatId);
        return result;
    }
}