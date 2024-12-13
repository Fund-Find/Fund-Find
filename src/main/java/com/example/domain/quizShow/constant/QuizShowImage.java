package com.example.domain.quizShow.constant;

import com.example.domain.quizShow.entity.QuizShowCategoryEnum;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum QuizShowImage {
    INVESTMENT("/images/quizShow/Investment.jpg", QuizShowCategoryEnum.INVESTMENT),
    SAVINGS("/images/quizShow/Savings.jpg", QuizShowCategoryEnum.SAVINGS),
    STOCK_MARKET("/images/quizShow/StockMarket.jpg", QuizShowCategoryEnum.STOCK_MARKET),
    REAL_ESTATE("/images/quizShow/RealEstate.jpg", QuizShowCategoryEnum.REAL_ESTATE),
    CRYPTOCURRENCY("/images/quizShow/Cryptocurrency.jpg", QuizShowCategoryEnum.CRYPTOCURRENCY),
    INSURANCE("/images/quizShow/Insurance.jpg", QuizShowCategoryEnum.INSURANCE);

    private final String imagePath;
    private final QuizShowCategoryEnum category;

    QuizShowImage(String imagePath, QuizShowCategoryEnum category) {
        this.imagePath = imagePath;
        this.category = category;
    }

    public static String getImagePathByCategory(QuizShowCategoryEnum category) {
        return Arrays.stream(QuizShowImage.values())
                .filter(image -> image.getCategory().equals(category))
                .findFirst()
                .map(QuizShowImage::getImagePath)
                .orElse(INVESTMENT.getImagePath()); // 기본 이미지 경로
    }
}