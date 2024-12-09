package com.example.domain.quizShow.entity;

public enum QuizTypeEnum {
    MULTIPLE_CHOICE("객관식"),
    SUBJECTIVE("주관식"),
    TRUE_FALSE("OX"),
    SHORT_ANSWER("단답형");

    private final String description;

    QuizTypeEnum(String description) {
        this.description = description;
    }
}