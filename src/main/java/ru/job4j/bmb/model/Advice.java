package ru.job4j.bmb.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "mb_advice")
public class Advice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean isGoodMood;

    public Advice(String text, boolean isGoodMood) {
        this.text = text;
        this.isGoodMood = isGoodMood;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isGoodMood() {
        return isGoodMood;
    }

    public void setGoodMood(boolean goodMood) {
        isGoodMood = goodMood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Advice advice = (Advice) o;
        return Objects.equals(id, advice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
