package com.example.global.initData;

import com.example.domain.quizShow.service.QuizShowService;
import com.example.domain.quizShow.service.QuizCategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Init {
    @Bean
    CommandLineRunner initData(QuizShowService quizShowService, QuizCategoryService quizCategoryService) {
        return args -> {
//            quizCategoryService.write("1번 타입");
//            quizCategoryService.write("2번 타입");
//            quizCategoryService.write("3번 타입");
//            quizCategoryService.write("4번 타입");
//            quizCategoryService.write("5번 타입");
//            quizCategoryService.write("6번 타입");
//
//            quizShowService.write("제목1", "설명1", 5, 10, 0);
//            quizShowService.write("제목2", "설명2", 10, 30, 0);
//            quizShowService.write("제목3", "설명3", 15, 55, 0);
//            quizShowService.write("제목4", "설명4", 5, 20, 0);
//            quizShowService.write("제목5", "설명5", 15, 15, 0);
        };
    }
}
