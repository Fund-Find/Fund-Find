package com.example.domain.quizShow.constant;

public enum QuizTypeEnum {
    MULTIPLE_CHOICE(1L, "객관식"),
    SUBJECTIVE(2L, "주관식"),
    TRUE_FALSE(3L, "OX"),
    SHORT_ANSWER(4L, "단답형");

    private final Long id;
    private final String description;

    QuizTypeEnum(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public Long getId() {
        return id;
    }

    // ID로 Enum 찾기
    public static QuizTypeEnum findById(Long id) {
        for (QuizTypeEnum type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid quiz type id: " + id);
    }
}


