package com.example.domain.quizShow.constant;

import lombok.Getter;
import java.util.Arrays;

@Getter
public enum QuizShowImage {
    INVESTMENT("/images/quizShow/Investment.jpg", "Investment"),
    SAVINGS("/images/quizShow/Savings.jpg", "Savings"),
    STOCK_MARKET("/images/quizShow/StockMarket.jpg", "Stock Market"),
    REAL_ESTATE("/images/quizShow/RealEstate.jpg", "Real Estate"),
    CRYPTOCURRENCY("/images/quizShow/Cryptocurrency.jpg", "Cryptocurrency"),
    INSURANCE("/images/quizShow/Insurance.jpg", "Insurance");

    private final String imagePath;
    private final String description;

    QuizShowImage(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public static QuizShowImage fromPath(String imagePath) {
        return Arrays.stream(QuizShowImage.values())
                .filter(image -> image.getImagePath().equals(imagePath))
                .findFirst()
                .orElse(INVESTMENT);
    }
}